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
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
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
	private ReportDefinitionService rds;
	
	@Autowired
	private ServiceContext serviceContext;
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
		executeDataSet("entityBasisMaps.xml");
		getCache().clear();
		//We have test that replaces this service with a mock, we need to always put it back
		serviceContext.setService(ReportDefinitionService.class, rds);
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
		final ReportData cachedData = new ReportData();
		final String cacheKey = grid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		final String cacheKeyOther = "other" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		getCache().put(cacheKey, cachedData);
		getCache().put(cacheKeyOther, cachedData);
		
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
		
		final ReportData cachedData = new ReportData();
		final String cacheKey = grid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		final String cacheKeyOther = "other" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		getCache().put(cacheKey, cachedData);
		getCache().put(cacheKeyOther, cachedData);
		
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
		
		final ReportData cachedData = new ReportData();
		final String cacheKey = grid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		final String cacheKeyOther = "other" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		getCache().put(cacheKey, cachedData);
		getCache().put(cacheKeyOther, cachedData);
		
		service.unretirePatientGrid(grid);
		
		assertFalse(grid.getRetired());
		assertNull(grid.getRetireReason());
		assertNull(grid.getRetiredBy());
		assertNull(grid.getDateRetired());
		
		assertNull(getCache().get(cacheKey));
		assertNotNull(getCache().get(cacheKeyOther));
	}
	
	@Test
	public void evaluate_shouldEvaluateThePatientGridAndCacheTheResults() {
		PatientGrid patientGrid = service.getPatientGrid(1);
		ReportData reportData = service.evaluate(patientGrid);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertEquals(reportData, getCache().get(cacheKey).get());
		SimpleDataSet dataset = (SimpleDataSet) reportData.getDataSets().get("patientData");
		assertEquals(3, dataset.getRows().size());
		Patient patient = ps.getPatient(2);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(47, dataset.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		Map<String, Object> obs = (Map) dataset.getColumnValue(patient.getId(), "weight");
		assertEquals(84.0, obs.get("value"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("SINGLE", obs.get("value"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "cd4");
		assertEquals(1060.0, obs.get("value"));
		Location location = locationService.getLocation(4000);
		assertEquals(location.getName(), dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		
		patient = ps.getPatient(6);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(46, dataset.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "weight");
		assertEquals(72.0, obs.get("value"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("SINGLE", obs.get("value"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "cd4");
		assertEquals(1080.0, obs.get("value"));
		location = locationService.getLocation(4001);
		assertEquals(location.getName(), dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		
		patient = ps.getPatient(7);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(45, dataset.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "weight");
		assertEquals(88.0, obs.get("value"));
		assertNull(dataset.getColumnValue(patient.getId(), "cd4"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("MARRIED", obs.get("value"));
		assertEquals(location.getName(), dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
	}
	
	@Test
	public void evaluate_shouldReturnCachedResultsAndNotEvaluateThePatientGrid() {
		final ReportData expectedCachedData = new ReportData();
		PatientGrid patientGrid = service.getPatientGrid(1);
		getCache().put(patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid(),
		    expectedCachedData);
		assertEquals(expectedCachedData, service.evaluate(patientGrid));
	}
	
	@Test
	public void evaluate_shouldIgnoreCachedResultsOfAnotherUserAndEvaluateThePatientGrid() {
		final ReportData expectedCachedData = new ReportData();
		PatientGrid patientGrid = service.getPatientGrid(1);
		getCache().put("another-user-uuid" + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid(),
		    expectedCachedData);
		ReportData reportData = service.evaluate(patientGrid);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNotEquals(expectedCachedData, service.evaluate(patientGrid));
		assertEquals(reportData, getCache().get(cacheKey).get());
	}
	
	@Test
	public void evaluate_shouldNotCacheEmptyReportData() {
		PatientGrid patientGrid = service.getPatientGrid(2);
		//Remove any filters so that our cohort remains empty
		patientGrid.getColumns().forEach(c -> c.getFilters().clear());
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNull(getCache().get(cacheKey));
		
		ReportData reportData = service.evaluate(patientGrid);
		
		SimpleDataSet dataset = (SimpleDataSet) reportData.getDataSets().get("patientData");
		assertTrue(dataset.getRows().isEmpty());
		assertNull(getCache().get(cacheKey));
	}
	
	@Test
	public void evaluate_shouldNotCacheReportDataIfThereIsNoAuthenticatedUserAfterTheReportIsEvaluated() throws Exception {
		ReportDefinitionService mockRds = Mockito.mock(ReportDefinitionService.class);
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNull(getCache().get(cacheKey));
		ReportData expectedReport = new ReportData();
		SimpleDataSet simpleDataSet = new SimpleDataSet(null, null);
		simpleDataSet.addRow(new DataSetRow());
		expectedReport.getDataSets().put("patientData", simpleDataSet);
		Mockito.doAnswer(invocation -> {
			Context.logout();
			assertNull(Context.getAuthenticatedUser());
			return expectedReport;
		}).when(mockRds).evaluate(any(ReportDefinition.class), any(EvaluationContext.class));
		
		Context.addProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
		serviceContext.setService(ReportDefinitionService.class, mockRds);
		ReportData reportData;
		try {
			reportData = service.evaluate(patientGrid);
		}
		finally {
			Context.removeProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
			serviceContext.setService(ReportDefinitionService.class, rds);
		}
		
		assertEquals(expectedReport, reportData);
		assertNull(getCache().get(cacheKey));
	}
	
	@Test
	public void evaluate_shouldNotCacheReportDataIfThereIsNoAuthenticatedUserBeforeTheReportIsEvaluated() throws Exception {
		ReportDefinitionService mockRds = Mockito.mock(ReportDefinitionService.class);
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String cacheKey = patientGrid.getUuid() + CACHE_KEY_SEPARATOR + Context.getAuthenticatedUser().getUuid();
		assertNull(getCache().get(cacheKey));
		Context.logout();
		assertNull(Context.getAuthenticatedUser());
		ReportData expectedReport = new ReportData();
		expectedReport.getDataSets().put("patientData", new SimpleDataSet(null, null));
		Mockito.when(mockRds.evaluate(any(ReportDefinition.class), any(EvaluationContext.class))).thenReturn(expectedReport);
		ReportData reportData;
		Context.addProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
		serviceContext.setService(ReportDefinitionService.class, mockRds);
		try {
			reportData = service.evaluate(patientGrid);
		}
		finally {
			Context.removeProxyPrivilege(PRIV_MANAGE_PATIENT_GRIDS);
			serviceContext.setService(ReportDefinitionService.class, rds);
		}
		
		assertEquals(expectedReport, reportData);
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
		
		ReportData reportData = service.evaluate(patientGrid);
		
		SimpleDataSet dataset = (SimpleDataSet) reportData.getDataSets().get("patientData");
		assertEquals(2, dataset.getRows().size());
		Patient patient = ps.getPatient(patientId2);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		Map<String, Object> obs = (Map) dataset.getColumnValue(patient.getId(), "civilStatus");
		assertEquals("SINGLE", obs.get("value"));
		
		patient = ps.getPatient(patientId6);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		obs = (Map) dataset.getColumnValue(patient.getId(), "civilStatus");
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
	
}
