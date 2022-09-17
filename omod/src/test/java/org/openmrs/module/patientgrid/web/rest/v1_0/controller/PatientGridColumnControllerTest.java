package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientGridColumnControllerTest extends BasePatientGridRestControllerTest {
	
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
	
	//@Test
	public void shouldAddANewColumnToThePatientGrid() throws Exception {
		long initialCount = getAllCount();
		SimpleObject column = new SimpleObject();
		column.add("type", "column");
		column.add("name", "nick name");
		column.add("datatype", "NAME");
		
		handle(newPostRequest(getURI(), column));
		
		assertEquals(++initialCount, getAllCount());
	}
	
	@Test
	public void shouldUpdateAnExistingPatientGridColumn() throws Exception {
		long initialCount = getAllCount();
		final String newName = "New Name";
		assertNotEquals(newName, service.getPatientGridColumnByUuid(COLUMN_UUID).getName());
		final String json = "{ \"name\":\"" + newName + "\" }";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		assertEquals(newName, service.getPatientGridColumnByUuid(COLUMN_UUID).getName());
		assertEquals(initialCount, getAllCount());
	}
	
	@Test
	public void shouldRemoveAnExistingPatientGridForADeleteRequest() throws Exception {
		long initialCount = getAllCount();
		assertNotNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "test")));
		
		assertNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		assertEquals(--initialCount, getAllCount());
	}
	
	@Test
	public void shouldDeleteAnExistingPatientGridForAPurgeRequest() throws Exception {
		long initialCount = getAllCount();
		assertNotNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		
		assertNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		assertEquals(--initialCount, getAllCount());
	}
	
}
