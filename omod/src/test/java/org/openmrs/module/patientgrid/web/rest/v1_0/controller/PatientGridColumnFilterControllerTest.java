package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientGridColumnFilterControllerTest extends BasePatientGridRestControllerTest {
	
	private static String GRID_UUID = "2d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	private static String COLUMN_UUID = "1f6c993e-c2cc-11de-8d13-0010c6dffd0b";
	
	private static String FILTER_UUID = "1f6c993e-c2cc-11de-8d13-0010c6dffd0c";
	
	@Autowired
	private PatientGridService service;
	
	@Rule
	public ExpectedException ee = ExpectedException.none();
	
	@Override
	public String getURI() {
		return "patientgrid/" + GRID_UUID + "/filter";
	}
	
	@Override
	public String getUuid() {
		return FILTER_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getPatientGridColumnByUuid(COLUMN_UUID).getFilters().size();
	}
	
	@Test
	public void shouldAddANewFilterToAColumnInTheGrid() throws Exception {
		long initialCount = getAllCount();
		SimpleObject filter = new SimpleObject();
		filter.add("name", "male filter");
		filter.add("patientGridColumn", COLUMN_UUID);
		filter.add("operand", "M");
		
		deserialize(handle(newPostRequest(getURI(), filter)));
		
		assertEquals(++initialCount, getAllCount());
	}
	
	@Test
	public void shouldUpdateAnExistingFilter() throws Exception {
		long initialCount = getAllCount();
		final String newName = "New Name";
		assertNotEquals(newName, service.getPatientGridColumnFilterByUuid(FILTER_UUID).getName());
		final String json = "{ \"name\":\"" + newName + "\" }";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		assertEquals(newName, service.getPatientGridColumnFilterByUuid(FILTER_UUID).getName());
		assertEquals(initialCount, getAllCount());
	}
	
	@Test
	public void shouldRemoveAnExistingFilterFromTheColumnInTheGridForADeleteRequest() throws Exception {
		long initialCount = getAllCount();
		assertNotNull(service.getPatientGridColumnFilterByUuid(FILTER_UUID));
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "test")));
		
		assertNull(service.getPatientGridColumnFilterByUuid(FILTER_UUID));
		assertEquals(--initialCount, getAllCount());
	}
	
	@Test
	public void shouldRemoveAnExistingFilterFromTheColumnInTheGridForAPurgeRequest() throws Exception {
		long initialCount = getAllCount();
		assertNotNull(service.getPatientGridColumnFilterByUuid(FILTER_UUID));
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		
		assertNull(service.getPatientGridColumnFilterByUuid(FILTER_UUID));
		assertEquals(--initialCount, getAllCount());
	}
	
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
}
