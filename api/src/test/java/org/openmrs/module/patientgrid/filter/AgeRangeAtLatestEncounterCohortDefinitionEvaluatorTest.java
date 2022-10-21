package org.openmrs.module.patientgrid.filter;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.filter.definition.AgeRangeAtLatestEncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class AgeRangeAtLatestEncounterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
		
		AgeRangeAtLatestEncounterCohortDefinition def = new AgeRangeAtLatestEncounterCohortDefinition();
		def.setEncounterType(new EncounterType(101));
		def.setAgeRanges(asList(new AgeRange(48, null)));
		
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContextPersistantCache());
		
		Assert.assertTrue(evaluatedCohort.isEmpty());
		
		def.setAgeRanges(asList(new AgeRange(45, 45)));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContextPersistantCache());
		
		assertEquals(1, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		
		def.setAgeRanges(asList(new AgeRange(46, 47)));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContextPersistantCache());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(2)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		
		def.setAgeRanges(asList(new AgeRange(45, 47)));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContextPersistantCache());
		
		assertEquals(3, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(2)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
	}
	
}
