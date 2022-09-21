package org.openmrs.module.patientgrid.web.rest;

import java.util.List;
import java.util.Map;

import org.openmrs.module.patientgrid.PatientGrid;

public abstract class BasePatientGridData {
	
	private PatientGrid patientGrid;
	
	private List<Map<String, Object>> report;
	
	public BasePatientGridData(PatientGrid patientGrid, List<Map<String, Object>> report) {
		this.patientGrid = patientGrid;
		this.report = report;
	}
	
	/**
	 * Gets the patientGrid
	 *
	 * @return the patientGrid
	 */
	public PatientGrid getPatientGrid() {
		return patientGrid;
	}
	
	/**
	 * Gets the report
	 *
	 * @return the report
	 */
	public List<Map<String, Object>> getReport() {
		return report;
	}
	
}
