package org.openmrs.module.patientgrid.filter;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LocationCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private CohortDefinitionService cohortDefService;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Before
	public void setup() {
		executeDataSet("entityBasisMaps.xml");
	}
	
	@Test
	public void evaluate_shouldReturnACohortOfPatientAssignedToTheSpecifiedLocations() throws Exception {
		LocationCohortDefinition def = new LocationCohortDefinition();
		def.setLocations(Arrays.asList(ls.getLocation(4001)));
		
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		Assert.assertEquals(2, evaluatedCohort.activeMembershipSize());
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		
		def.setLocations(Arrays.asList(ls.getLocation(4002)));
		
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		Assert.assertEquals(2, evaluatedCohort.activeMembershipSize());
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
		
		def.setLocations(Arrays.asList(ls.getLocation(4001), ls.getLocation(4002)));
		
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		Assert.assertEquals(3, evaluatedCohort.activeMembershipSize());
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		Assert.assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
	}
	
}
