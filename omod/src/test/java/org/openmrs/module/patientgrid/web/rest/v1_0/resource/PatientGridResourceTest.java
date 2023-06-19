package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PatientGridResourceTest extends BaseDelegatingResourceTest<PatientGridResource, PatientGrid> {
	
	private static String TEST_UUID = "1d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
	}
	
	@Override
	public PatientGrid newObject() {
		return Context.getService(PatientGridService.class).getPatientGridByUuid(TEST_UUID);
	}
	
	@Override
	public String getDisplayProperty() {
		return "My Patients";
	}
	
	@Override
	public String getUuidProperty() {
		return TEST_UUID;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals(PatientGridConstants.PROP_SHARED, false);
		assertPropNotPresent(PatientGridConstants.PROP_COLUMNS);
		assertPropNotPresent("auditInfo");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals(PatientGridConstants.PROP_SHARED, false);
		assertPropPresent(PatientGridConstants.PROP_COLUMNS);
		assertPropPresent("auditInfo");
	}
	
}
