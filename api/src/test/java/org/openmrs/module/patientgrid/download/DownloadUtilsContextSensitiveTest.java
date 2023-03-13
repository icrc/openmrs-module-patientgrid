package org.openmrs.module.patientgrid.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.patientgrid.PatientGridConstants.COLUMN_UUID;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DownloadUtilsContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	private final String utcTimeZone = "UTC";
	
	@Autowired
	private PatientGridService service;
	
	@Autowired
	@Qualifier("patientService")
	private PatientService ps;
	
	@Autowired
	private LocationService locationService;
	
	@Before
	public void setup() {
		executeDataSet("entityBasisMaps.xml");
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}
	
	@Test
	public void evaluate_shouldReturnDownloadReportDataForTheSpecifiedPatientGrid() {
		//prepare
		PatientGrid patientGrid = service.getPatientGrid(1);
		final String oldTimeZone = System.getProperty("user.timezone");
		System.setProperty("user.timezone", utcTimeZone);
		
		//action
		ExtendedDataSet extendedDataSet = DownloadUtils.evaluate(patientGrid);
		SimpleDataSet dataset = extendedDataSet.getSimpleDataSet();
		
		//assert
		assertEquals(
		    "{\"code\":\"customDaysInclusive\",\"fromDate\":\"2022-04-01 00:00:00\",\"toDate\":\"2022-12-31 00:00:00\"}",
		    extendedDataSet.getPeriodOperand());
		assertEquals("1648771200000-1672531199999", extendedDataSet.getUsedDateRange());
		//the cohort should be 4 but as we have a initial cohort with 3 patients ( 2,6,7) only these 3 will be returned.
		assertEquals(3, dataset.getRows().size());
		Patient patient = ps.getPatient(2);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(47, dataset.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		Location location = locationService.getLocation(4000);
		assertEquals(location.getName(), dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		final String initialEncTypeUuid = "19218f76-6c39-45f4-8efa-4c5c6c199f50";
		final String followUpEncTypeUuid = "29218f76-6c39-45f4-8efa-4c5c6c199f50";
		final String weightColumnUuid = "4e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		final String civilStatusColumn = "5e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		final String cd4ColumnUuid = "6e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		List<Map<String, Map>> encounters = (List) dataset.getColumnValue(patient.getId(), initialEncTypeUuid);
		assertEquals(3, encounters.size());
		Map<String, Map> columnUuidAndObsMap = encounters.get(2);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(82), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		columnUuidAndObsMap = encounters.get(1);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(85), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(84), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		Concept civilStatusConcept = Context.getConceptService().getConcept(5);
		assertEquals(civilStatusConcept.getUuid(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("uuid"));
		assertEquals(civilStatusConcept.getDisplayString(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("display"));
		encounters = (List) dataset.getColumnValue(patient.getId(), followUpEncTypeUuid);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(83), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		assertEquals(Double.valueOf(1060), columnUuidAndObsMap.get(cd4ColumnUuid).get("value"));
		
		patient = ps.getPatient(6);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(46, dataset.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		location = locationService.getLocation(4001);
		assertEquals(location.getName(), dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		encounters = (List) dataset.getColumnValue(patient.getId(), initialEncTypeUuid);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(72), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		assertEquals(civilStatusConcept.getUuid(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("uuid"));
		assertEquals(civilStatusConcept.getDisplayString(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("display"));
		encounters = (List) dataset.getColumnValue(patient.getId(), followUpEncTypeUuid);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(1080), columnUuidAndObsMap.get(cd4ColumnUuid).get("value"));
		
		patient = ps.getPatient(7);
		location = locationService.getLocation(4002);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(45, dataset.getColumnValue(patient.getId(), "ageAtInitial"));
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		assertEquals(location.getName(), dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		encounters = (List) dataset.getColumnValue(patient.getId(), initialEncTypeUuid);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(88), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		civilStatusConcept = Context.getConceptService().getConcept(6);
		assertEquals(civilStatusConcept.getUuid(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("uuid"));
		assertEquals(civilStatusConcept.getDisplayString(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("display"));
		encounters = (List) dataset.getColumnValue(patient.getId(), followUpEncTypeUuid);
		assertEquals(1, encounters.size());
		assertTrue(encounters.get(0).isEmpty());
		
		// a patient with no values
		patient = ps.getPatient(8);
		assertNull("the patient 8 is not in the initial cohort and excluded from the dataset",
		    dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		
		System.setProperty("user.timezone", oldTimeZone);
	}
	
	@Test
	public void evaluate_shouldFiltersPatientsWhenGeneratingDownloadReport() {
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
		
		SimpleDataSet dataset = DownloadUtils.evaluate(patientGrid).getSimpleDataSet();
		
		assertEquals(2, dataset.getRows().size());
		Patient patient = ps.getPatient(2);
		assertNotNull(dataset.getColumnValue(patient.getPatientId(), COLUMN_UUID));
		patient = ps.getPatient(6);
		assertNotNull(dataset.getColumnValue(patient.getPatientId(), COLUMN_UUID));
		patient = ps.getPatient(7);
		assertNull(dataset.getColumnValue(patient.getPatientId(), COLUMN_UUID));
	}
	
}
