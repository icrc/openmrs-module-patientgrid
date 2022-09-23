package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.junit.Before;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientGridColumnResourceTest extends BaseDelegatingResourceTest<PatientGridColumnResource, PatientGridColumn> {
	
	private static String COLUMN_UUID = "1e6c993e-c2cc-11de-8d13-0010c6dffd0b";
	
	@Autowired
	private PatientGridService service;
	
	@Before
	public void setupBasePatientGridRestControllerTest() {
		executeDataSet("patientGrids.xml");
	}
	
	@Override
	public PatientGridColumn newObject() {
		return service.getPatientGridColumnByUuid(COLUMN_UUID);
	}
	
	@Override
	public String getDisplayProperty() {
		return "name";
	}
	
	@Override
	public String getUuidProperty() {
		return COLUMN_UUID;
	}
	
	private void validateRepresentation() {
		PatientGridColumn column = newObject();
		assertPropEquals("name", column.getName());
		assertPropEquals("description", column.getDescription());
		assertPropEquals("datatype", column.getDatatype());
		assertPropPresent("filters");
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		validateRepresentation();
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		validateRepresentation();
	}
	
}
