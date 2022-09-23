package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.junit.Before;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientGridColumnFilterResourceTest extends BaseDelegatingResourceTest<PatientGridColumnFilterResource, PatientGridColumnFilter> {
	
	private static String FILTER_UUID = "1f6c993e-c2cc-11de-8d13-0010c6dffd0c";
	
	@Autowired
	private PatientGridService service;
	
	@Before
	public void setupBasePatientGridRestControllerTest() {
		executeDataSet("patientGrids.xml");
	}
	
	@Override
	public PatientGridColumnFilter newObject() {
		return service.getPatientGridColumnFilterByUuid(FILTER_UUID);
	}
	
	@Override
	public String getDisplayProperty() {
		return "is male";
	}
	
	@Override
	public String getUuidProperty() {
		return FILTER_UUID;
	}
	
	private void validateRepresentation() {
		PatientGridColumnFilter column = newObject();
		assertPropEquals("name", column.getName());
		assertPropPresent("column");
		assertPropEquals("operand", column.getOperand());
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		validateRepresentation();
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
