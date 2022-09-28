package org.openmrs.module.patientgrid.api.db;

import java.util.List;

import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;

/**
 * Provides database access methods to manage {@link PatientGrid} objects
 */
public interface PatientGridDAO {
	
	/**
	 * @see org.openmrs.module.patientgrid.api.PatientGridService#getPatientGrid(Integer)
	 */
	PatientGrid getPatientGrid(Integer patientGridId);
	
	/**
	 * @see org.openmrs.module.patientgrid.api.PatientGridService#getPatientGridByUuid(String)
	 */
	PatientGrid getPatientGridByUuid(String uuid);
	
	/**
	 * @see org.openmrs.module.patientgrid.api.PatientGridService#getPatientGrids(boolean)
	 */
	List<PatientGrid> getPatientGrids(boolean includeRetired);
	
	/**
	 * @see org.openmrs.module.patientgrid.api.PatientGridService#savePatientGrid(PatientGrid)
	 */
	PatientGrid savePatientGrid(PatientGrid patientGrid);
	
	/**
	 * @see org.openmrs.module.patientgrid.api.PatientGridService#getPatientGridColumnByUuid(String)
	 */
	PatientGridColumn getPatientGridColumnByUuid(String uuid);
	
	/**
	 * @see org.openmrs.module.patientgrid.api.PatientGridService#getPatientGridColumnFilterByUuid(String)
	 */
	PatientGridColumnFilter getPatientGridColumnFilterByUuid(String uuid);
	
}
