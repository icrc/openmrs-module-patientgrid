package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openmrs.module.patientgrid.AgeAtEncounterPatientGridColumn;
import org.openmrs.module.patientgrid.ObsPatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
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
		column.add("datatype", ColumnDatatype.NAME);
		
		SimpleObject result = deserialize(handle(newPostRequest(getURI(), column)));
		
		assertEquals(++initialCount, getAllCount());
		PatientGridColumn saveColumn = service.getPatientGridColumnByUuid(Util.getByPath(result, "uuid").toString());
		assertEquals(PatientGridColumn.class, saveColumn.getClass());
	}
	
	//@Test
	public void shouldAddANewObsColumnToThePatientGrid() throws Exception {
		long initialCount = getAllCount();
		SimpleObject column = new SimpleObject();
		column.add("type", "obscolumn");
		column.add("name", "height");
		column.add("datatype", ColumnDatatype.OBS);
		column.add("encounterType", "19218f76-6c39-45f4-8efa-4c5c6c199f50");
		column.add("concept", "95312123-e0c2-466d-b6b1-cb6e990d0d65");
		
		SimpleObject result = deserialize(handle(newPostRequest(getURI(), column)));
		
		assertEquals(++initialCount, getAllCount());
		PatientGridColumn savedColumn = service.getPatientGridColumnByUuid(Util.getByPath(result, "uuid").toString());
		assertEquals(ObsPatientGridColumn.class, savedColumn.getClass());
	}
	
	//@Test
	public void shouldAddANewAgeColumnToThePatientGrid() throws Exception {
		long initialCount = getAllCount();
		SimpleObject column = new SimpleObject();
		column.add("type", "agecolumn");
		column.add("name", "height");
		column.add("datatype", ColumnDatatype.OBS);
		column.add("encounterType", "19218f76-6c39-45f4-8efa-4c5c6c199f50");
		column.add("convertToAgeRange", true);
		
		SimpleObject result = deserialize(handle(newPostRequest(getURI(), column)));
		
		assertEquals(++initialCount, getAllCount());
		PatientGridColumn savedColumn = service.getPatientGridColumnByUuid(Util.getByPath(result, "uuid").toString());
		assertEquals(AgeAtEncounterPatientGridColumn.class, savedColumn.getClass());
	}
	
	//@Test
	public void shouldAddANewEncounterDateColumnToThePatientGrid() throws Exception {
		long initialCount = getAllCount();
		SimpleObject column = new SimpleObject();
		column.add("type", "encounterdatecolumn");
		column.add("name", "encDate");
		column.add("datatype", ColumnDatatype.ENC_DATE);
		column.add("encounterType", "19218f76-6c39-45f4-8efa-4c5c6c199f50");
		
		SimpleObject result = deserialize(handle(newPostRequest(getURI(), column)));
		
		assertEquals(++initialCount, getAllCount());
		PatientGridColumn savedColumn = service.getPatientGridColumnByUuid(Util.getByPath(result, "uuid").toString());
		assertEquals(AgeAtEncounterPatientGridColumn.class, savedColumn.getClass());
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
	public void shouldAddAFilterToAnExistingColumn() throws Exception {
		long initialFilterCount = service.getPatientGridColumnByUuid(COLUMN_UUID).getFilters().size();
		SimpleObject filter = new SimpleObject();
		filter.add("name", "equal 12");
		filter.add("operand", "12");
		SimpleObject payload = new SimpleObject();
		payload.add("filters", new SimpleObject[] { filter });
		
		handle(newPostRequest(getURI() + "/" + getUuid(), payload));
		
		assertEquals(++initialFilterCount, service.getPatientGridColumnByUuid(COLUMN_UUID).getFilters().size());
	}
	
	@Test
	public void shouldUpdateAnExistingObsColumn() throws Exception {
		long initialCount = getAllCount();
		final String newName = "New Name";
		final String uuid = "4e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		ObsPatientGridColumn column = (ObsPatientGridColumn) service.getPatientGridColumnByUuid(uuid);
		assertNotEquals(newName, column.getName());
		final String json = "{ \"name\":\"" + newName + "\" }";
		
		handle(newPostRequest(getURI() + "/" + uuid, json));
		
		assertEquals(newName, service.getPatientGridColumnByUuid(uuid).getName());
		assertEquals(initialCount, getAllCount());
	}
	
	@Test
	public void shouldRemoveAnExistingColumnFromTheGridForADeleteRequest() throws Exception {
		long initialCount = getAllCount();
		assertNotNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "test")));
		
		assertNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		assertEquals(--initialCount, getAllCount());
	}
	
	@Test
	public void shouldRemoveAnExistingColumnFromTheGridForAPurgeRequest() throws Exception {
		long initialCount = getAllCount();
		assertNotNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		
		assertNull(service.getPatientGridColumnByUuid(COLUMN_UUID));
		assertEquals(--initialCount, getAllCount());
	}
	
}
