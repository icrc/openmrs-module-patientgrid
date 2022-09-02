package org.openmrs.module.patientgrid.filter;

import static java.util.Arrays.asList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ObsForLatestEncounterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private CohortDefinitionService cohortDefService;
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}
	
	@Test
	public void evaluate_shouldReturnACohortOfPatientsWithObsMatchingTheSpecifiedValuesFromTheLatestEncounter()
	    throws Exception {
		ObsForLatestEncounterCohortDefinition def = new ObsForLatestEncounterCohortDefinition();
		def.setPropertyName("valueNumeric");
		def.setConcept(new Concept(5089));
		def.setEncounterType(new EncounterType(101));
		def.setValues(asList(72.0, 84.0, 88.0));
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		Assert.assertEquals(3, evaluatedCohort.activeMembershipSize());
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(2)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		
		//Try with a narrowed down value list
		def.setValues(asList(72.0, 84.0));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		Assert.assertEquals(2, evaluatedCohort.activeMembershipSize());
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(2)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		
		//Try with a concept of a different datatype
		def.setPropertyName("valueCoded");
		def.setConcept(new Concept(4));
		def.setValues(asList(Context.getConceptService().getConcept(5)));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		Assert.assertEquals(2, evaluatedCohort.activeMembershipSize());
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(2)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
	}
	
}
