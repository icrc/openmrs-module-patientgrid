package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.webservices.rest.web.RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.openmrs.module.patientgrid.AgeAtEncounterPatientGridColumn;
import org.openmrs.module.patientgrid.ObsPatientGridColumn;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientGridControllerTest extends BasePatientGridRestControllerTest {
	
	private static String TEST_UUID = "1d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	@Autowired
	private PatientGridService service;
	
	@Override
	public String getURI() {
		return "patientgrid";
	}
	
	@Override
	public String getUuid() {
		return TEST_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getPatientGrids(false).size();
	}
	
	@Test
	public void shouldReturnAllPatientGridsIfIncludeAllIsSetToTrue() throws Exception {
		Parameter includeAll = new Parameter(REQUEST_PROPERTY_FOR_INCLUDE_ALL, "true");
		assertEquals(getAllCount() + 1, Util.getResultsSize(deserialize(handle(newGetRequest(getURI(), includeAll)))));
	}
	
	@Test
	public void shouldCreateANewPatientGrid() throws Exception {
		long initialCount = getAllCount();
		SimpleObject nameColumn = new SimpleObject();
		nameColumn.add("type", "column");
		nameColumn.add("name", "name");
		nameColumn.add("datatype", ColumnDatatype.NAME);
		SimpleObject weightColumn = new SimpleObject();
		weightColumn.add("type", "obscolumn");
		weightColumn.add("name", "weight");
		weightColumn.add("datatype", ColumnDatatype.OBS);
		weightColumn.add("concept", "c607c80f-1ea9-4da3-bb88-6276ce8868dd");
		weightColumn.add("encounterType", "19218f76-6c39-45f4-8efa-4c5c6c199f50");
		SimpleObject ageColumn = new SimpleObject();
		ageColumn.add("type", "agecolumn");
		ageColumn.add("name", "age");
		ageColumn.add("datatype", ColumnDatatype.ENC_AGE);
		ageColumn.add("encounterType", "19218f76-6c39-45f4-8efa-4c5c6c199f50");
		ageColumn.add("hidden", true);
		SimpleObject filter = new SimpleObject();
		filter.add("name", "equal 12");
		filter.add("operand", "12");
		ageColumn.add("filters", new SimpleObject[] { filter });
		SimpleObject grid = new SimpleObject();
		grid.put("name", "test");
		grid.put("description", "test description");
		grid.put("shared", true);
		grid.add("columns", Arrays.asList(nameColumn, weightColumn, ageColumn));
		SimpleObject result = deserialize(handle(newPostRequest(getURI(), grid)));
		assertEquals(++initialCount, getAllCount());
		PatientGrid savePatientGrid = service.getPatientGridByUuid(Util.getByPath(result, "uuid").toString());
		assertEquals(3, savePatientGrid.getColumns().size());
		Iterator<PatientGridColumn> columns = savePatientGrid.getColumns().iterator();
		assertTrue(columns.next() instanceof PatientGridColumn);
		assertTrue(columns.next() instanceof ObsPatientGridColumn);
		AgeAtEncounterPatientGridColumn ageGridColumn = (AgeAtEncounterPatientGridColumn) columns.next();
		assertEquals(1, ageGridColumn.getFilters().size());
	}
	
	@Test
	public void shouldUpdateAnExistingPatientGrid() throws Exception {
		long initialCount = getAllCount();
		final String newName = "My Other List";
		assertNotEquals(newName, service.getPatientGridByUuid(TEST_UUID).getName());
		final String json = "{ \"name\":\"" + newName + "\" }";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		assertEquals(newName, service.getPatientGridByUuid(TEST_UUID).getName());
		assertEquals(initialCount, getAllCount());
	}
	
	@Test
	public void shouldVoidAnExistingPatientGrid() throws Exception {
		PatientGrid grid = service.getPatientGridByUuid(TEST_UUID);
		assertFalse(grid.getRetired());
		assertNull(grid.getRetireReason());
		final String reason = "testing";
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", reason)));
		
		grid = service.getPatientGridByUuid(TEST_UUID);
		assertTrue(service.getPatientGridByUuid(TEST_UUID).getRetired());
		assertEquals(reason, grid.getRetireReason());
	}
	
}
