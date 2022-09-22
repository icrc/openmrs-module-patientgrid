package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

public class PatientGridReportControllerTest extends BasePatientGridRestControllerTest {
	
	private static String GRID_UUID = "1d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	@Override
	public String getURI() {
		return "patientgrid/" + GRID_UUID + "/report";
	}
	
	@Override
	public String getUuid() {
		return null;
	}
	
	@Override
	public long getAllCount() {
		return 1;
	}
	
	@Override
	public void shouldGetAll() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI())));
		assertEquals(getAllCount(), Util.getResultsSize(result));
		Map report = (Map) ((List) Util.getByPath(result, "results")).get(0);
		assertEquals(GRID_UUID, Util.getByPath(report, new String[] { "patientGrid", "uuid" }));
		assertEquals(3, ((List) Util.getByPath(report, "report")).size());
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
