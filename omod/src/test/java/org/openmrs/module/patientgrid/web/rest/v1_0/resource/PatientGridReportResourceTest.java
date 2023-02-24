package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import java.util.ArrayList;

import org.junit.Before;
import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.web.rest.PatientGridReport;
import org.openmrs.module.patientgrid.web.rest.ReportMetadata;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientGridReportResourceTest extends BaseDelegatingResourceTest<PatientGridReportResource, PatientGridReport> {
	
	private static String GRID_UUID = "1d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	@Autowired
	private PatientGridService service;
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
	}
	
	@Override
	public PatientGridReport newObject() {
		PatientGrid patientGrid = service.getPatientGridByUuid(GRID_UUID);
		ExtendedDataSet dataSet = new ExtendedDataSet();
		dataSet.setUsedDateRange("periodUsedStartEndDate");
		dataSet.setTruncated(true);
		dataSet.setInitialRowsCount(1000);
		dataSet.setRowsCountLimit(100);
		return new PatientGridReport(new ReportMetadata(dataSet), patientGrid, new ArrayList());
	}
	
	@Override
	public String getDisplayProperty() {
		return null;
	}
	
	@Override
	public String getUuidProperty() {
		return null;
	}
	
	private void validateRepresentation() {
		assertPropPresent("patientGrid");
		assertPropPresent("report");
		assertPropPresent("reportMetadata");
	}
	
	@Override
	public void validateRefRepresentation() {
		validateRepresentation();
	}
	
	@Override
	public void validateDefaultRepresentation() {
		validateRepresentation();
	}
	
	@Override
	public void validateFullRepresentation() {
		validateRepresentation();
	}
	
}
