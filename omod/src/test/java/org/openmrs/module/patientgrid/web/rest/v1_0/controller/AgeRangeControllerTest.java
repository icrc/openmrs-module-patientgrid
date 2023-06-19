package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import org.junit.Test;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

public class AgeRangeControllerTest extends BasePatientGridRestControllerTest {
	
	@Override
	public String getURI() {
		return "agerange";
	}
	
	@Override
	public String getUuid() {
		return null;
	}
	
	@Override
	public long getAllCount() {
		return PatientGridUtils.getAgeRanges().size();
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
	
}
