package org.openmrs.module.patientgrid.download;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class AllEncountersPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientDataService patientDataService;
	
	@Autowired
	private EncounterService encounterService;
	
	@Autowired
	private PatientGridService patientGridService;
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}
	
	@Test
	public void evaluate_shouldReturnTheObsDataForAllEncountersOfASpecificTypeForEachPatient() throws Exception {
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		final Integer patientId7 = 7;
		final Integer patientId8 = 8;
		EvaluationContext context = new EvaluationContextPersistantCache();
		context.setBaseCohort(new Cohort(asList(patientId2, patientId6, patientId7, patientId8)));
		
		AllEncountersPatientDataDefinition def = new AllEncountersPatientDataDefinition();
		def.setEncounterType(new EncounterType(101));
		PatientGrid patientGrid = patientGridService.getPatientGrid(1);
		def.setPatientGrid(patientGrid);
		
		EvaluatedPatientData data = patientDataService.evaluate(def, context);
		
		assertEquals(4, data.getData().size());
		List<Map<String, Map>> encounters = (List) data.getData().get(patientId2);
		assertEquals(3, encounters.size());
		final String weightColumnUuid = "4e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		final String civilStatusColumn = "5e6c993e-c2cc-11de-8d13-0010c6dffd0b";
		Map<String, Map> columnUuidAndObsMap = encounters.get(0);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(82), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		columnUuidAndObsMap = encounters.get(1);
		assertEquals(1, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(85), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		columnUuidAndObsMap = encounters.get(2);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(84), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		Concept civilStatusConcept = Context.getConceptService().getConcept(5);
		assertEquals(civilStatusConcept.getUuid(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("uuid"));
		assertEquals(civilStatusConcept.getDisplayString(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("display"));
		
		encounters = (List) data.getData().get(patientId6);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(72), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		assertEquals(civilStatusConcept.getUuid(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("uuid"));
		assertEquals(civilStatusConcept.getDisplayString(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("display"));
		
		encounters = (List) data.getData().get(patientId7);
		assertEquals(1, encounters.size());
		columnUuidAndObsMap = encounters.get(0);
		assertEquals(2, columnUuidAndObsMap.size());
		assertEquals(Double.valueOf(88), columnUuidAndObsMap.get(weightColumnUuid).get("value"));
		civilStatusConcept = Context.getConceptService().getConcept(6);
		assertEquals(civilStatusConcept.getUuid(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("uuid"));
		assertEquals(civilStatusConcept.getDisplayString(),
		    ((Map) columnUuidAndObsMap.get(civilStatusColumn).get("value")).get("display"));
		
		encounters = (List) data.getData().get(patientId8);
		assertEquals(1, encounters.size());
		assertTrue(encounters.get(0).isEmpty());
	}
	
}
