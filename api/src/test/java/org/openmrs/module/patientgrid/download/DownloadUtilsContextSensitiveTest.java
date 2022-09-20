package org.openmrs.module.patientgrid.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.patientgrid.PatientGridConstants.COLUMN_UUID;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DownloadUtilsContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientGridService service;
	
	@Autowired
	@Qualifier("patientService")
	private PatientService ps;
	
	@Autowired
	private LocationService locationService;
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
		executeDataSet("entityBasisMaps.xml");
	}
	
	@Test
	public void evaluate_shouldReturnDownloadReportDataForTheSpecifiedPatientGrid() {
		PatientGrid patientGrid = service.getPatientGrid(1);
		
		SimpleDataSet dataset = DownloadUtils.evaluate(patientGrid);
		
		assertEquals(3, dataset.getRows().size());
		Patient patient = ps.getPatient(2);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(47, ((Age) dataset.getColumnValue(patient.getId(), "ageAtInitial")).getFullYears().intValue());
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		Location location = locationService.getLocation(4000);
		assertEquals(location, dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		final String initialEncTypeUuid = "19218f76-6c39-45f4-8efa-4c5c6c199f50";
		final String followUpEncTypeUuid = "29218f76-6c39-45f4-8efa-4c5c6c199f50";
		final String weightColumnUuid = "4e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		final String civilStatusColumn = "5e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		final String cd4ColumnUuid = "6e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		List<Map<String, Map>> encounters = (List) dataset.getColumnValue(patient.getId(), initialEncTypeUuid);
		assertEquals(3, encounters.size());
		Map<String, Map> columnUuidAndObsMap = encounters.get(0);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(82), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		columnUuidAndObsMap = encounters.get(1);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(85), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		columnUuidAndObsMap = encounters.get(2);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(84), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		assertEquals("SINGLE", columnUuidAndObsMap.get(civilStatusColumn).get("value"));
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
		assertEquals(46, ((Age) dataset.getColumnValue(patient.getId(), "ageAtInitial")).getFullYears().intValue());
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		location = locationService.getLocation(4001);
		assertEquals(location, dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		encounters = (List) dataset.getColumnValue(patient.getId(), initialEncTypeUuid);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(72), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		assertEquals("SINGLE", columnUuidAndObsMap.get(civilStatusColumn).get("value"));
		encounters = (List) dataset.getColumnValue(patient.getId(), followUpEncTypeUuid);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(1080), columnUuidAndObsMap.get(cd4ColumnUuid).get("value"));
		
		patient = ps.getPatient(7);
		assertEquals(patient.getUuid(), dataset.getColumnValue(patient.getId(), COLUMN_UUID));
		assertEquals(patient.getPersonName().getFullName(), dataset.getColumnValue(patient.getId(), "name"));
		assertEquals(patient.getGender(), dataset.getColumnValue(patient.getId(), "gender"));
		assertEquals(45, ((Age) dataset.getColumnValue(patient.getId(), "ageAtInitial")).getFullYears().intValue());
		assertEquals("18+", dataset.getColumnValue(patient.getId(), "ageCategory"));
		assertEquals(location, dataset.getColumnValue(patient.getId(), "structure"));
		assertEquals(location.getCountry(), dataset.getColumnValue(patient.getId(), "country"));
		encounters = (List) dataset.getColumnValue(patient.getId(), initialEncTypeUuid);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(88), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		assertEquals("MARRIED", columnUuidAndObsMap.get(civilStatusColumn).get("value"));
		encounters = (List) dataset.getColumnValue(patient.getId(), followUpEncTypeUuid);
		assertEquals(1, encounters.size());
		assertTrue(encounters.get(0).isEmpty());
	}
	
}
