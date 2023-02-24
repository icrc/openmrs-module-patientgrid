package org.openmrs.module.patientgrid.web.rest;

import java.util.List;
import java.util.Map;

import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGrid;

public class PatientGridDownload extends BasePatientGridData {
	
	public PatientGridDownload(ReportMetadata reportMetadata, PatientGrid patientGrid, List<Map<String, Object>> report) {
		super(reportMetadata, patientGrid, report);
	}
	
}
