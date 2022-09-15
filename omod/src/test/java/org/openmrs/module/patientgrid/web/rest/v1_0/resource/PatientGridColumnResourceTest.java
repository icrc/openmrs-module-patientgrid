package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.junit.Test;
import org.openmrs.module.patientgrid.web.rest.v1_0.controller.BasePatientGridRestControllerTest;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

public class PatientGridColumnResourceTest extends BasePatientGridRestControllerTest {
	
	@Override
	public String getURI() {
		return "patientgrid/grid-uuid/column";
	}
	
	@Override
	public String getUuid() {
		return "column-uuid";
	}
	
	@Override
	public long getAllCount() {
		return 0;
	}
	
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetDefaultByUuid() throws Exception {
		super.shouldGetDefaultByUuid();
	}
	
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetRefByUuid() throws Exception {
		super.shouldGetRefByUuid();
	}
	
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetFullByUuid() throws Exception {
		super.shouldGetFullByUuid();
	}
	
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
}
