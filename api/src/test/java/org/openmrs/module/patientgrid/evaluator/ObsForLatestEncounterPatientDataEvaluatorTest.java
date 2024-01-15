package org.openmrs.module.patientgrid.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.definition.ObsForLatestEncounterPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ObsForLatestEncounterPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private PatientDataService patientDataService;

	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;

	@Autowired
	@Qualifier("obsService")
	private ObsService os;

	@Before
	public void setup() {
		executeDataSet("entityBasisMaps.xml");
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}

	@Test
	public void evaluate_shouldReturnTheObsForTheMostRecentEncounters() throws Exception {
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		final Integer patientId8 = 8;
		EncounterType encounterType = new EncounterType(101);
		EvaluationContextPersistantCache context = new EvaluationContextPersistantCache();
		context.setBaseCohort(new Cohort(asList(patientId8)));
		assertFalse(PatientGridUtils.getEncounters(encounterType, context, null, true, null).isEmpty());
		context = new EvaluationContextPersistantCache();
		context.setBaseCohort(new Cohort(asList(patientId2, patientId6)));
		Concept concept = cs.getConcept(5089);
		ObsForLatestEncounterPatientDataDefinition obsDef = new ObsForLatestEncounterPatientDataDefinition();
		obsDef.setEncounterType(encounterType);
		obsDef.setConcept(concept);

		EvaluatedPatientData data = patientDataService.evaluate(obsDef, context);
		assertEquals(2, data.getData().size());
		assertEquals(os.getObs(1004), data.getData().get(patientId2));
		assertEquals(os.getObs(1006), data.getData().get(patientId6));
		assertNull(data.getData().get(patientId8));
	}

}
