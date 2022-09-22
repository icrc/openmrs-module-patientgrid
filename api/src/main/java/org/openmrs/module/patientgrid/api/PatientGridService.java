package org.openmrs.module.patientgrid.api;

import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_CONDITION_EXP;
import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_KEY_EXP;
import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_MANAGER_NAME;
import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_NAME_GRID_REPORTS;
import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_UNLESS_EXP;
import static org.openmrs.module.patientgrid.PatientGridConstants.PRIV_MANAGE_PATIENT_GRIDS;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;
import org.openmrs.module.reporting.report.ReportData;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Provides methods to manage {@link PatientGrid} objects
 */
@CacheConfig(cacheManager = CACHE_MANAGER_NAME, cacheNames = CACHE_NAME_GRID_REPORTS)
public interface PatientGridService extends OpenmrsService {
	
	/**
	 * Gets a patient grid that matches the specified id
	 *
	 * @param patientGridId the id to match against
	 * @return the patient grid that matches the specified id
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	PatientGrid getPatientGrid(Integer patientGridId);
	
	/**
	 * Gets a patient grid that matches the specified uuid
	 *
	 * @param uuid the uuid to match against
	 * @return the patient grid that matches the specified uuid
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	PatientGrid getPatientGridByUuid(String uuid);
	
	/**
	 * Gets the patient grids
	 *
	 * @param includeRetired specifies whether retired grids should included or not
	 * @return list of patient grids
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	List<PatientGrid> getPatientGrids(boolean includeRetired);
	
	/**
	 * Saves the {@link PatientGrid} object
	 * 
	 * @param patientGrid
	 * @return the saved patient grid
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	@CacheEvict(key = CACHE_KEY_EXP)
	PatientGrid savePatientGrid(PatientGrid patientGrid);
	
	/**
	 * Marks the specified patient grid as retired
	 *
	 * @param patientGrid the patient grid to retire
	 * @param retireReason for retiring
	 * @return the retired patient grid
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	PatientGrid retirePatientGrid(PatientGrid patientGrid, String retireReason);
	
	/**
	 * Marks the specified patient grid as not retired
	 *
	 * @param patientGrid the patient grid to unretire
	 * @return the none retired patient grid
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	PatientGrid unretirePatientGrid(PatientGrid patientGrid);
	
	/**
	 * Gets a patient grid column that matches the specified uuid
	 *
	 * @param uuid the uuid to match against
	 * @return the patient grid column that matches the specified uuid
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	PatientGridColumn getPatientGridColumnByUuid(String uuid);
	
	/**
	 * Gets a patient grid column filter that matches the specified uuid
	 *
	 * @param uuid the uuid to match against
	 * @return the patient grid column filter that matches the specified uuid
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	PatientGridColumnFilter getPatientGridColumnFilterByUuid(String uuid);
	
	/**
	 * Evaluates the specified {@link PatientGrid}
	 *
	 * @param patientGrid the patient grid to evaluate
	 * @return the generated {@link ReportData}
	 */
	@Authorized(PRIV_MANAGE_PATIENT_GRIDS)
	@Cacheable(key = CACHE_KEY_EXP, condition = CACHE_CONDITION_EXP, unless = CACHE_UNLESS_EXP)
	ReportData evaluate(PatientGrid patientGrid);
	
}
