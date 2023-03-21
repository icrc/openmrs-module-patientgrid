package org.openmrs.module.patientgrid.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientgrid.*;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.api.db.PatientGridDAO;
import org.openmrs.module.patientgrid.filter.ObjectWithDateRange;
import org.openmrs.module.patientgrid.filter.PatientGridFilterUtils;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.openmrs.module.patientgrid.PatientGridConstants.GP_ROWS_COUNT_LIMIT;

@Transactional(readOnly = true)
public class PatientGridServiceImpl extends BaseOpenmrsService implements PatientGridService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientGridServiceImpl.class);
	
	private PatientGridDAO dao;
	
	/**
	 * Normally we should use {@link #mergeCohort(Cohort, Cohort)} but it compares also the date of the
	 * {@link CohortMembership}. In our case, we just want to compare the patientIds.
	 *
	 * @param initCohort the cohort that will be modified and will contain the intersection
	 * @param staticCohort the static cohort to merge with
	 * @return initCohort will only the common patients
	 */
	protected static Cohort mergeCohort(Cohort initCohort, Cohort staticCohort) {
		if (initCohort.isEmpty()) {
			return initCohort;
		}
		if (staticCohort.isEmpty()) {
			initCohort.getMemberships().clear();
		} else {
			Set<Integer> patientIds = staticCohort.getMemberships().stream().map(CohortMembership::getPatientId)
			        .collect(Collectors.toSet());
			initCohort.setMemberships(initCohort.getMemberships().stream()
			        .filter(cohortMembership -> patientIds.contains(cohortMembership.getPatientId()))
			        .collect(Collectors.toSet()));
		}
		return initCohort;
		
	}
	
	public static ExtendedDataSet createExtendedDataSet(PatientGrid patientGrid, String clientTimezone,
	        PatientDataSetDefinition dataSetDef) throws EvaluationException {
		EvaluationContextPersistantCache context = new EvaluationContextPersistantCache();
		ObjectWithDateRange<Cohort> cohortWithPeriod = PatientGridFilterUtils.filterPatients(patientGrid, context,
		    clientTimezone);
		Cohort cohort = cohortWithPeriod == null ? null : cohortWithPeriod.getObject();
		//if not filters done by PatientGridFilterUtils, we will use the static cohort
		if (cohort == null) {
			cohort = patientGrid.getCohort();
		}
		//if a static cohort is present and a new cohort is filtered, we should calculate the intersection
		//the static cohort is not asked by business for now.
		if (cohort != null && patientGrid.getCohort() != null) {
			cohort = mergeCohort(cohort, patientGrid.getCohort());
		}
		if (cohort == null) {
			cohort = new Cohort();
		}
		
		int limit = 100;
		String rowLimit = Context.getAdministrationService().getGlobalProperty(GP_ROWS_COUNT_LIMIT);
		if (StringUtils.isNotBlank(rowLimit)) {
			try {
				limit = Integer.parseInt(rowLimit);
			}
			catch (NumberFormatException e) {
				LOGGER.warn("The row limit '{}' defined in the global property '{}' is not supported", rowLimit,
				    GP_ROWS_COUNT_LIMIT);
				throw new RuntimeException(e);
			}
		}
		int initCohortSize = cohort.getMemberships().size();
		context.setBaseCohort(cohort);
		context.limitAndSortCohortBasedOnEncounterDate(limit);
		
		SimpleDataSet ds;
		//if the cohort is empty -> do nothing
		if (cohort.isEmpty()) {
			ds = new SimpleDataSet(dataSetDef, context);
		} else {
			ds = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(dataSetDef, context);
		}
		ExtendedDataSet extendedDataSet = new ExtendedDataSet(ds,
		        cohortWithPeriod == null ? null : cohortWithPeriod.getDateRange());
		
		extendedDataSet.setRowsCountLimit(limit);
		extendedDataSet.setInitialRowsCount(initCohortSize);
		if (limit < initCohortSize) {
			extendedDataSet.setTruncated(true);
		}
		context.clearPersistentCache();
		return extendedDataSet;
	}
	
	/**
	 * Sets the dao
	 *
	 * @param dao the dao to set
	 */
	public void setDao(PatientGridDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see PatientGridService#getPatientGrid(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientGrid getPatientGrid(Integer patientGridId) {
		PatientGrid patientGrid = dao.getPatientGrid(patientGridId);
		if (!canCurrentUserSeePatientGrid(patientGrid)) {
			throw new APIException("PatientGrid.error.privilege");
		}
		return patientGrid;
	}
	
	/**
	 * @see PatientGridService#getPatientGridByUuid(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientGrid getPatientGridByUuid(String uuid) {
		PatientGrid patientGrid = dao.getPatientGridByUuid(uuid);
		if (!canCurrentUserSeePatientGrid(patientGrid)) {
			throw new APIException("PatientGrid.error.privilege");
		}
		return patientGrid;
	}
	
	/**
	 * @see PatientGridService#getPatientGrids(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PatientGrid> getPatientGrids(boolean includeRetired) {
		List<PatientGrid> patientGrids = dao.getPatientGrids(includeRetired);
		final User user = Context.getAuthenticatedUser();
		//Could it happen ?
		if (user == null) {
			return new ArrayList<>();
		}
		if (user.isSuperUser()) {
			return patientGrids;
		}
		return patientGrids.stream().filter(p -> userCanSeePatientGrid(p, user)).collect(Collectors.toList());
	}
	
	private boolean canCurrentUserSeePatientGrid(PatientGrid patientGrid) {
		return userCanSeePatientGrid(patientGrid, Context.getAuthenticatedUser());
	}
	
	/**
	 * @param patientGrid the grid
	 * @param user the current user
	 * @return true if current user can see all encounterType and all location used by the grid
	 */
	private boolean userCanSeePatientGrid(PatientGrid patientGrid, User user) {
		if (patientGrid == null) {
			return true;
		}
		Set<EncounterType> encounterTypes = PatientGridFilterUtils.extractEncounterType(patientGrid);
		boolean cantSeeEncounterTypes = encounterTypes.stream().anyMatch(e -> !userCanSeeEncounterType(e, user));
		if (cantSeeEncounterTypes) {
			return false;
		}
		return PatientGridFilterUtils.canSeeLocations(patientGrid);
	}
	
	private boolean userCanSeeEncounterType(EncounterType encounterType, User user) {
		Privilege privilege = encounterType.getViewPrivilege();
		//If the encounter privilege is null, everyone can see and edit the encounter.
		if (privilege == null) {
			return true;
		}
		return user.hasPrivilege(privilege.getPrivilege());
	}
	
	/**
	 * @see PatientGridService#savePatientGrid(PatientGrid)
	 */
	@Transactional
	@Override
	public PatientGrid savePatientGrid(PatientGrid patientGrid) {
		if (!canCurrentUserModifyGrid(patientGrid)) {
			throw new APIException("PatientGrid.error.cantSave");
		}
		return dao.savePatientGrid(patientGrid);
	}
	
	private boolean canCurrentUserModifyGrid(PatientGrid patientGrid) {
		User user = Context.getAuthenticatedUser();
		return user.isSuperUser() || patientGrid.getOwner() == null || user.equals(patientGrid.getOwner());
	}
	
	/**
	 * @see PatientGridService#retirePatientGrid(PatientGrid, String)
	 */
	@Transactional
	@Override
	public PatientGrid retirePatientGrid(PatientGrid patientGrid, String retireReason) {
		return Context.getService(PatientGridService.class).savePatientGrid(patientGrid);
	}
	
	/**
	 * @see PatientGridService#unretirePatientGrid(PatientGrid)
	 */
	@Transactional
	@Override
	public PatientGrid unretirePatientGrid(PatientGrid patientGrid) {
		return Context.getService(PatientGridService.class).savePatientGrid(patientGrid);
	}
	
	/**
	 * @see PatientGridService#getPatientGridColumnByUuid(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientGridColumn getPatientGridColumnByUuid(String uuid) {
		return dao.getPatientGridColumnByUuid(uuid);
	}
	
	/**
	 * @see PatientGridService#getPatientGridColumnFilterByUuid(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientGridColumnFilter getPatientGridColumnFilterByUuid(String uuid) {
		return dao.getPatientGridColumnFilterByUuid(uuid);
	}
	
	/**
	 * @see PatientGridService#evaluate(PatientGrid)
	 */
	@Transactional(readOnly = true)
	public ExtendedDataSet evaluate(PatientGrid patientGrid) {
		if (patientGrid == null) {
			return null;
		}
		LOGGER.debug("Generating report for patient grid: {}", patientGrid);
		if (!canCurrentUserSeePatientGrid(patientGrid)) {
			throw new APIException("PatientGrid.error.privilege");
		}
		try {
			final String clientTimezone = PatientGridUtils.getCurrentUserTimeZone();
			PatientDataSetDefinition dataSetDef = PatientGridUtils.createPatientDataSetDefinition(patientGrid, true,
			    clientTimezone);
			return createExtendedDataSet(patientGrid, clientTimezone, dataSetDef);
		}
		catch (EvaluationException e) {
			throw new APIException("PatientGrid.error.cantEvaluate", new Object[] { patientGrid }, e);
		}
	}
	
	/**
	 * @see PatientGridService#evaluateIgnoreCache(PatientGrid)
	 */
	@Override
	@Transactional(readOnly = true)
	public ExtendedDataSet evaluateIgnoreCache(PatientGrid patientGrid) {
		if (!canCurrentUserSeePatientGrid(patientGrid)) {
			throw new APIException("PatientGrid.error.privilege");
		}
		return evaluate(patientGrid);
	}
	
}
