package org.openmrs.module.patientgrid.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_KEY_SEPARATOR;
import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_MANAGER_NAME;
import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_NAME_GRID_REPORTS;
import static org.openmrs.module.patientgrid.PatientGridConstants.COLUMN_UUID;
import static org.openmrs.module.patientgrid.PatientGridConstants.PRIV_MANAGE_PATIENT_GRIDS;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class PatientGridServiceTest extends BaseModuleContextSensitiveTest {
	
	private static String TEST_UUID = "1d6c993e-c2cc-11de-8d13-0010c6dffd0a";
	
	@Autowired
	private PatientGridService service;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	@Qualifier("patientService")
	private PatientService ps;
	
	@Autowired
	@Qualifier(CACHE_MANAGER_NAME)
	private CacheManager cacheManager;
	
	@Autowired
	private DataSetDefinitionService dsds;
	
	@Autowired
	private ServiceContext serviceContext;
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
		executeDataSet("entityBasisMaps.xml");
		getCache().clear();
		//We have test that replaces this service with a mock, we need to always put it back
		serviceContext.setService(DataSetDefinitionService.class, dsds);
	}
	
	private Cache getCache() {
		return cacheManager.getCache(CACHE_NAME_GRID_REPORTS);
	}
	
	@Test
	public void getPatientGrid_shouldReturnThePatientGridMatchingTheSpecifiedId() {
		assertEquals(TEST_UUID, service.getPatientGrid(1).getUuid());
	}
	
	@Test
	public void getPatientGridByUuid_shouldReturnThePatientGridMatchingTheSpecifiedUuid() {
		assertEquals(1, service.getPatientGridByUuid(TEST_UUID).getId().intValue());
	}
	
	@Test
	public void getPatientGridByUuid_shouldReturnNullIfNoPatientGridMatchesTheSpecifiedUuid() {
		assertNull(service.getPatientGridByUuid("bad-uuid"));
	}
	
	@Test
	public void getPatientGrids_shouldReturnTheNonRetiredPatientGrids() {
		List<PatientGrid> grids = service.getPatientGrids(false);
		assertEquals(2, grids.size());
		for (PatientGrid patientGrid : grids) {
			assertFalse(patientGrid.getRetired());
		}
	}
	
	@Test
	public void getPatientGrids_shouldReturnTheAllPatientGridsIncludingRetiredOnes() {
		List<PatientGrid> grids = service.getPatientGrids(true);
		assertEquals(3, grids.size());
		assertFalse(grids.get(0).getRetired());
		assertFalse(grids.get(1).getRetired());
		assertTrue(grids.get(2).getRetired());
	}
	
	@Test
	public void savePatientGrid_shouldSaveThePatientGridToTheDatabase() {
		int originalCount = service.getPatientGrids(true).size();
		PatientGrid grid = new PatientGrid();
		grid.setName("test");
		grid.setDescription("test description");
		grid.setCohort(new Cohort(101));
		grid.addColumn(new PatientGridColumn("name", ColumnDatatype.NAME));
		final SimpleDataSet dataSet = new SimpleDataSet(null, null);
		final String cacheKey = grid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		final String cacheKeyOther = "other" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		getCache().put(cacheKey, dataSet);
		getCache().put(cacheKeyOther, dataSet);
		
		service.savePatientGrid(grid);
		
		assertNotNull(grid.getId());
		assertEquals(++originalCount, service.getPatientGrids(true).size());
		assertNull(getCache().get(cacheKey));
		assertNotNull(getCache().get(cacheKeyOther));
	}
	
	@Test
	public void retirePatientGrid_shouldRetireThePatientGrid() {
		final String reason = "some reason";
		PatientGrid grid = service.getPatientGrid(1);
		assertFalse(grid.getRetired());
		assertNull(grid.getRetireReason());
		assertNull(grid.getRetiredBy());
		assertNull(grid.getDateRetired());
		
		final SimpleDataSet cachedDataSet = new SimpleDataSet(null, null);
		final String cacheKey = grid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		final String cacheKeyOther = "other" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		getCache().put(cacheKey, cachedDataSet);
		getCache().put(cacheKeyOther, cachedDataSet);
		
		service.retirePatientGrid(grid, reason);
		
		assertTrue(grid.getRetired());
		assertEquals(reason, grid.getRetireReason());
		assertNotNull(grid.getRetiredBy());
		assertNotNull(grid.getDateRetired());
		
		assertNull(getCache().get(cacheKey));
		assertNotNull(getCache().get(cacheKeyOther));
	}
	
	@Test
	public void unretirePatientGrid_shouldUnretireThePatientGrid() {
		PatientGrid grid = service.getPatientGrid(3);
		assertTrue(grid.getRetired());
		assertNotNull(grid.getRetireReason());
		assertNotNull(grid.getRetiredBy());
		assertNotNull(grid.getDateRetired());
		
		final SimpleDataSet cachedDataSet = new SimpleDataSet(null, null);
		final String cacheKey = grid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		final String cacheKeyOther = "other" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		getCache().put(cacheKey, cachedDataSet);
		getCache().put(cacheKeyOther, cachedDataSet);
		
		service.unretirePatientGrid(grid);
		
		assertFalse(grid.getRetired());
		assertNull(grid.getRetireReason());
		assertNull(grid.getRetiredBy());
		assertNull(grid.getDateRetired());
		
		assertNull(getCache().get(cacheKey));
		assertNotNull(getCache().get(cacheKeyOther));
	}
	
	@Test
	public void evaluate_shouldEvaluateThePatientGridAndCacheTheDataSet() {
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNull(getCache().get(cacheKey));
		
		SimpleDataSet dataSet = service.evaluate(patientGrid);
		
		assertEquals(3, dataSet.getRows().size());
		assertEquals(dataSet.getRows().size(), ((SimpleDataSet) getCache().get(cacheKey).get()).getRows().size());
		Patient patient = ps.getPatient(2);
		assertEquals(patient.getUuid(), dataSet.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataSet.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataSet.getColumnValue(patient.getId(), "gender"));
		assertEquals(47, dataSet.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataSet.getColumnValue(patient.getId(), "ageCategory"));
		Map<String, Object> obs = (Map) dataSet.getColumnValue(patient.getId(), "weight");
		assertEquals(84.0, obs.get("value"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("SINGLE", obs.get("value"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "cd4");
		assertEquals(1060.0, obs.get("value"));
		Location location = locationService.getLocation(4000);
		assertEquals(location.getName(), dataSet.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataSet.getColumnValue(patient.getId(), "country"));
		
		patient = ps.getPatient(6);
		assertEquals(patient.getUuid(), dataSet.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataSet.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataSet.getColumnValue(patient.getId(), "gender"));
		assertEquals(46, dataSet.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataSet.getColumnValue(patient.getId(), "ageCategory"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "weight");
		assertEquals(72.0, obs.get("value"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("SINGLE", obs.get("value"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "cd4");
		assertEquals(1080.0, obs.get("value"));
		location = locationService.getLocation(4001);
		assertEquals(location.getName(), dataSet.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataSet.getColumnValue(patient.getId(), "country"));
		
		patient = ps.getPatient(7);
		assertEquals(patient.getUuid(), dataSet.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataSet.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataSet.getColumnValue(patient.getId(), "gender"));
		assertEquals(45, dataSet.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataSet.getColumnValue(patient.getId(), "ageCategory"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "weight");
		assertEquals(88.0, obs.get("value"));
		assertNull(dataSet.getColumnValue(patient.getId(), "cd4"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("MARRIED", obs.get("value"));
		assertEquals(location.getName(), dataSet.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataSet.getColumnValue(patient.getId(), "country"));
	}
	
	@Test
	public void evaluate_shouldReturnCachedDataSetsAndNotEvaluateThePatientGrid() {
		final Integer patientId = 8888;
		final String columnName = "name";
		final String name = "Test Patient";
		DataSetColumn column = new DataSetColumn(columnName, null, String.class);
		final SimpleDataSet cachedDataSet = new SimpleDataSet(null, null);
		cachedDataSet.addColumnValue(patientId, column, name);
		PatientGrid patientGrid = service.getPatientGrid(1);
		getCache().put(patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid(),
		    cachedDataSet);
		
		SimpleDataSet newDataSet = service.evaluate(patientGrid);
		
		assertEquals(cachedDataSet.getRows().size(), newDataSet.getRows().size());
		assertEquals(name, newDataSet.getColumnValue(patientId, columnName));
	}
	
	@Test
	public void evaluate_shouldIgnoreCachedDataSetsAndEvaluateThePatientGridAndCacheTheNewDataSet() {
		final String name = "Test Patient";
		DataSetColumn column = new DataSetColumn(name, null, String.class);
		final SimpleDataSet cachedDataSet = new SimpleDataSet(null, null);
		cachedDataSet.addColumnValue(8888, column, "Test Patient");
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		getCache().put(cacheKey, cachedDataSet);
		
		SimpleDataSet newDataSet = service.evaluateIgnoreCache(patientGrid);
		
		assertNotEquals(cachedDataSet.getRows().size(), newDataSet.getRows().size());
		assertEquals(newDataSet.getRows().size(), ((SimpleDataSet) getCache().get(cacheKey).get()).getRows().size());
	}
	
	@Test
	public void evaluate_shouldIgnoreCachedDataSetOfAnotherUserAndEvaluateThePatientGrid() {
		final SimpleDataSet cachedDataSet = new SimpleDataSet(null, null);
		PatientGrid patientGrid = service.getPatientGrid(1);
		getCache().put("another-user-uuid" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid(), cachedDataSet);
		
		SimpleDataSet newDataSet = service.evaluate(patientGrid);
		
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNotEquals(cachedDataSet.getRows().size(), newDataSet.getRows().size());
		assertEquals(newDataSet.getRows().size(), ((SimpleDataSet) getCache().get(cacheKey).get()).getRows().size());
	}
	
	@Test
	public void evaluate_shouldNotCacheEmptyReportData() {
		PatientGrid patientGrid = service.getPatientGrid(2);
		//Remove any filters so that our cohort remains empty
		patientGrid.getColumns().forEach(c -> c.getFilters().clear());
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNull(getCache().get(cacheKey));
		
		SimpleDataSet dataSet = service.evaluate(patientGrid);
		
		assertTrue(dataSet.getRows().isEmpty());
		assertNull(getCache().get(cacheKey));
	}
	
	@Test
	public void evaluate_shouldNotCacheDataSetIfThereIsNoAuthenticatedUserAfterTheDataSetIsEvaluated() throws Exception {
		assertNotNull(Context.getAuthenticatedUser());
		DataSetDefinitionService mockDsds = Mockito.mock(DataSetDefinitionService.class);
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNull(getCache().get(cacheKey));
		SimpleDataSet expectedDataSet = new SimpleDataSet(null, null);
		expectedDataSet.addRow(new DataSetRow());
		Mockito.doAnswer(invocation -> {
			Context.logout();
			assertNull(Context.getAuthenticatedUser());
			return expectedDataSet;
		}).when(mockDsds).evaluate(any(DataSetDefinition.class), any(EvaluationContext.class));
		
		Context.addProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
		serviceContext.setService(DataSetDefinitionService.class, mockDsds);
		SimpleDataSet dataSet;
		try {
			dataSet = service.evaluate(patientGrid);
		}
		finally {
			Context.removeProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
			serviceContext.setService(DataSetDefinitionService.class, dsds);
		}
		
		assertEquals(expectedDataSet, dataSet);
		assertNull(getCache().get(cacheKey));
	}
	
	@Test
	public void evaluate_shouldNotCacheDataSetIfThereIsNoAuthenticatedUserBeforeTheDataSetIsEvaluated() throws Exception {
		DataSetDefinitionService mockDsds = Mockito.mock(DataSetDefinitionService.class);
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNull(getCache().get(cacheKey));
		Context.logout();
		assertNull(Context.getAuthenticatedUser());
		SimpleDataSet expectedDataSet = new SimpleDataSet(null, null);
		Mockito.when(mockDsds.evaluate(any(DataSetDefinition.class), any(EvaluationContext.class)))
		        .thenReturn(expectedDataSet);
		SimpleDataSet dataSet;
		Context.addProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
		serviceContext.setService(DataSetDefinitionService.class, mockDsds);
		try {
			dataSet = service.evaluate(patientGrid);
		}
		finally {
			Context.removeProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
			serviceContext.setService(DataSetDefinitionService.class, dsds);
		}
		
		assertEquals(expectedDataSet, dataSet);
		assertNull(getCache().get(cacheKey));
	}
	
	@Test
	public void evaluate_shouldFiltersPatientsWhenEvaluatingAGridWithFilteredColumns() {
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		final Integer patientId7 = 7;//Female
		//The filters are for male and (married or single) patients
		PatientGrid patientGrid = service.getPatientGrid(2);
		boolean hasFilteredColumns = false;
		for (PatientGridColumn column : patientGrid.getColumns()) {
			if (!column.getFilters().isEmpty()) {
				hasFilteredColumns = true;
				break;
			}
		}
		assertTrue(hasFilteredColumns);
		assertTrue(patientGrid.getCohort().getActiveMemberships().isEmpty());
		Cohort cohort = new Cohort(Arrays.asList(patientId2, patientId6, patientId7));
		cohort.setName("test");
		cohort.setDescription("test");
		Context.getCohortService().saveCohort(cohort);
		patientGrid.setCohort(cohort);
		
		SimpleDataSet dataSet = service.evaluate(patientGrid);
		
		assertEquals(2, dataSet.getRows().size());
		Patient patient = ps.getPatient(patientId2);
		assertEquals(patient.getUuid(), dataSet.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataSet.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataSet.getColumnValue(patient.getId(), "gender"));
		Map<String, Object> obs = (Map) dataSet.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("SINGLE", obs.get("value"));
		
		patient = ps.getPatient(patientId6);
		assertEquals(patient.getUuid(), dataSet.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataSet.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataSet.getColumnValue(patient.getId(), "gender"));
		obs = (Map) dataSet.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("SINGLE", obs.get("value"));
	}
	
	@Test
	public void getPatientGridColumnByUuid_shouldReturnThePatientGridColumnMatchingTheSpecifiedUuid() {
		assertEquals(1, service.getPatientGridColumnByUuid("1e6c993e-c2cc-11de-8d13-0010c6dffd0b").getId().intValue());
	}
	
	@Test
	public void getPatientGridColumnByUuid_shouldReturnNullIfNoPatientGridColumnMatchesTheSpecifiedUuid() {
		assertNull(service.getPatientGridColumnByUuid("bad-uuid"));
	}
	
	@Test
	public void getPatientGridColumnFilterByUuid_shouldReturnTheFilterMatchingTheSpecifiedUuid() {
		assertEquals(1, service.getPatientGridColumnFilterByUuid("1f6c993e-c2cc-11de-8d13-0010c6dffd0c").getId().intValue());
	}
	
	@Test
	public void getPatientGridColumnFilterByUuid_shouldReturnNullIfNoFilterMatchesTheSpecifiedUuid() {
		assertNull(service.getPatientGridColumnFilterByUuid("bad-uuid"));
	}
	
}
