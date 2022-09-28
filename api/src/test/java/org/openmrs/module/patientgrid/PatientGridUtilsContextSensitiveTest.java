package org.openmrs.module.patientgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PatientGridUtilsContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientGridService service;
	
	@Autowired
	@Qualifier("encounterService")
	private EncounterService es;
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
		executeDataSet("entityBasisMaps.xml");
	}
	
	@Test
	public void getEncounters_shouldReturnTheMostRecentEncounterOfTheSpecifiedTypeForAPatient() throws Exception {
		Cohort cohort = new Cohort();
		final Integer patientId = 2;
		cohort.addMember(patientId);
		Map<Integer, Object> idsAndEncs = PatientGridUtils.getEncounters(new EncounterType(101), cohort, true);
		assertEquals(1, idsAndEncs.size());
		assertEquals(2004, ((Encounter) idsAndEncs.get(patientId)).getEncounterId().intValue());
	}
	
	@Test
	public void getEncounters_shouldReturnAllEncountersOfTheSpecifiedTypeForAPatient() throws Exception {
		Cohort cohort = new Cohort();
		final Integer patientId = 2;
		cohort.addMember(patientId);
		Map<Integer, Object> idsAndEncs = PatientGridUtils.getEncounters(new EncounterType(101), cohort, false);
		assertEquals(1, idsAndEncs.size());
		List<Encounter> encounters = (List) idsAndEncs.get(patientId);
		assertEquals(3, encounters.size());
		assertEquals(2001, encounters.get(0).getEncounterId().intValue());
		assertEquals(2005, encounters.get(1).getEncounterId().intValue());
		assertEquals(2004, encounters.get(2).getEncounterId().intValue());
	}
	
	@Test
	public void getEncounters_shouldReturnAnEmptyListIfThePatientHasNoMatchingEncounters() throws Exception {
		Cohort cohort = new Cohort();
		final Integer patientId = 8;
		cohort.addMember(patientId);
		assertTrue(PatientGridUtils.getEncounters(new EncounterType(102), cohort, true).isEmpty());
	}
	
}
