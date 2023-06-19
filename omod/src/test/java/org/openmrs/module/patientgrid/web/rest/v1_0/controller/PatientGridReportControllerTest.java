package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.openmrs.module.patientgrid.PatientGridConstants.*;
import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.PARAM_REFRESH;

public class PatientGridReportControllerTest extends BasePatientGridRestControllerTest {
	
	private static String GRID_UUID = "1d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	@Autowired
	private PatientGridService service;
	
	@Autowired
	@Qualifier(CACHE_MANAGER_NAME)
	private CacheManager cacheManager;
	
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
		assertEquals(4, ((Map) Util.getByPath(report, "reportMetadata")).size());
		assertEquals(false, Util.getByPath(report, new String[] { "reportMetadata", "truncated" }));
	}
	
	@Test
	public void shouldIgnoreCachedReportIfRefreshIsSetToTrue() throws Exception {
		DataSetColumn column = new DataSetColumn("name", null, String.class);
		final SimpleDataSet cachedDataSet = new SimpleDataSet(null, null);
		cachedDataSet.addColumnValue(8888, column, "Test Patient");
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		cacheManager.getCache(CACHE_NAME_GRID_REPORTS).put(cacheKey, cachedDataSet);
		
		Object result = deserialize(handle(newGetRequest(getURI(), new Parameter(PARAM_REFRESH, "true"))));
		Object report = ((List) Util.getByPath(result, "results")).get(0);
		assertNotEquals(cachedDataSet.getRows().size(), ((List) Util.getByPath(report, "report")).size());
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
