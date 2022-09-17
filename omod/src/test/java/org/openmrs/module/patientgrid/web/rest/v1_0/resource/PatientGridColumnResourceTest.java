package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.web.rest.v1_0.controller.BasePatientGridRestControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientGridColumnResourceTest extends BasePatientGridRestControllerTest {
	
	private static String GRID_UUID = "1d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	private static String COLUMN_UUID = "1e6c993e-c2cc-11de-8d13-0010c6dffd0b";
	
	@Autowired
	private PatientGridService service;
	
	@Override
	public String getURI() {
		return "patientgrid/" + GRID_UUID + "/column";
	}
	
	@Override
	public String getUuid() {
		return COLUMN_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getPatientGridByUuid(GRID_UUID).getColumns().size();
	}
	
}
