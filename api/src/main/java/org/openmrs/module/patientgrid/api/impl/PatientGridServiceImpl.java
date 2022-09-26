package org.openmrs.module.patientgrid.api.impl;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.api.db.PatientGridDAO;
import org.openmrs.module.patientgrid.filter.PatientGridFilterUtils;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class PatientGridServiceImpl extends BaseOpenmrsService implements PatientGridService {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridServiceImpl.class);
	
	private PatientGridDAO dao;
	
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
	public PatientGrid getPatientGrid(Integer patientGridId) {
		return dao.getPatientGrid(patientGridId);
	}
	
	/**
	 * @see PatientGridService#getPatientGridByUuid(String)
	 */
	@Override
	public PatientGrid getPatientGridByUuid(String uuid) {
		return dao.getPatientGridByUuid(uuid);
	}
	
	/**
	 * @see PatientGridService#getPatientGrids(boolean)
	 */
	@Override
	public List<PatientGrid> getPatientGrids(boolean includeRetired) {
		return dao.getPatientGrids(includeRetired);
	}
	
	/**
	 * @see PatientGridService#savePatientGrid(PatientGrid)
	 */
	@Transactional
	@Override
	public PatientGrid savePatientGrid(PatientGrid patientGrid) {
		return dao.savePatientGrid(patientGrid);
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
	public PatientGridColumn getPatientGridColumnByUuid(String uuid) {
		return dao.getPatientGridColumnByUuid(uuid);
	}
	
	/**
	 * @see PatientGridService#getPatientGridColumnFilterByUuid(String)
	 */
	@Override
	public PatientGridColumnFilter getPatientGridColumnFilterByUuid(String uuid) {
		return dao.getPatientGridColumnFilterByUuid(uuid);
	}
	
	/**
	 * @see PatientGridService#evaluate(PatientGrid)
	 */
	public SimpleDataSet evaluate(PatientGrid patientGrid) {
		if (log.isDebugEnabled()) {
			log.debug("Generating report for patient grid: " + patientGrid);
		}
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		try {
			PatientDataSetDefinition dataSetDef = PatientGridUtils.createPatientDataSetDefinition(patientGrid, true);
			EvaluationContext context = new EvaluationContext();
			Cohort cohort = PatientGridFilterUtils.filterPatients(patientGrid, context);
			if (cohort == null) {
				cohort = patientGrid.getCohort();
			}
			
			//TODO If cohort is not null but empty, return immediately
			
			context.setBaseCohort(cohort);
			
			SimpleDataSet ds = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(dataSetDef,
			    context);
			
			stopWatch.stop();
			
			if (log.isDebugEnabled()) {
				log.debug("Report for patient grid " + patientGrid + " completed in " + stopWatch.toString());
			}
			
			return ds;
		}
		catch (EvaluationException e) {
			throw new APIException("Failed to evaluate patient grid: " + patientGrid, e);
		}
	}
	
	/**
	 * @see PatientGridService#evaluateIgnoreCache(PatientGrid)
	 */
	@Override
	public SimpleDataSet evaluateIgnoreCache(PatientGrid patientGrid) {
		return evaluate(patientGrid);
	}
	
}
