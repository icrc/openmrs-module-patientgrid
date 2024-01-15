package org.openmrs.module.patientgrid.evaluator;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.definition.AgeAtLatestEncounterPatientDataDefinition;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class AgeAtLatestEncounterPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private PatientDataService patientDataService;

	@Before
	public void setup() {
		executeDataSet("entityBasisMaps.xml");
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}

	@Test
	public void evaluate_shouldReturnThePatientAge() throws Exception {
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		final Integer patientId7 = 7;
		final Integer patientId8 = 8;//Has encounter but no birthdate
		final Integer patientId999 = 999;//Has no encounter and no birthdate
		EvaluationContext context = new EvaluationContextPersistantCache();
		context.setBaseCohort(new Cohort(asList(patientId2, patientId6, patientId7, patientId8, patientId999)));

		AgeAtLatestEncounterPatientDataDefinition def = new AgeAtLatestEncounterPatientDataDefinition();
		def.setEncounterType(new EncounterType(101));
		EvaluatedPatientData data = patientDataService.evaluate(def, context);

		assertEquals(4, data.getData().size());
		assertEquals(47, ((Age) data.getData().get(patientId2)).getFullYears().intValue());
		assertEquals(46, ((Age) data.getData().get(patientId6)).getFullYears().intValue());
		assertEquals(45, ((Age) data.getData().get(patientId7)).getFullYears().intValue());
		assertNull(data.getData().get(patientId8));
		assertNull(data.getData().get(patientId999));
	}

}
