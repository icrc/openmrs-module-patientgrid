package org.openmrs.module.patientgrid.filter;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.module.datafilter.impl.api.DataFilterService;
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
	
	@Autowired
	private DataFilterService dfs;
	
	@Before
	public void setup() {
		executeDataSet("entityBasisMaps.xml");
	}
	
	private Location createLocation(String name) {
		Location l = new Location();
		l.setName(name);
		return ls.saveLocation(l);
	}
	
	@Test
	public void evaluate_shouldReturnACohortOfPatientAssignedToTheSpecifiedLocations() throws Exception {
		LocationCohortDefinition def = new LocationCohortDefinition();
		def.setLocations(asList(ls.getLocation(4001)));
		
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		
		def.setLocations(asList(ls.getLocation(4002)));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
		
		def.setLocations(asList(ls.getLocation(4001), ls.getLocation(4002)));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(3, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
	}
	
	@Test
	public void evaluate_shouldReturnACohortOfPatientAssignedToTheSpecifiedCountryLocations() throws Exception {
		LocationCohortDefinition def = new LocationCohortDefinition();
		def.setCountry(true);
		def.setLocations(asList(createLocation("Republic Of Uganda")));
		
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		assertTrue(evaluatedCohort.isEmpty());
		
		Location usa = createLocation("United States");
		def.setLocations(asList(usa));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		
		Location kenya = createLocation("Kenya");//should be case insensitive
		def.setLocations(asList(kenya));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
		
		def.setLocations(asList(usa, kenya));
		evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(3, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(6)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
	}
	
	@Test
	public void evaluate_shouldBeCaseInsensitiveWhenMatchingOnCountry() throws Exception {
		LocationCohortDefinition def = new LocationCohortDefinition();
		def.setCountry(true);
		def.setLocations(asList(createLocation("KENYA")));
		
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
	}
	
	@Test
	public void evaluate_shouldIgnoreALocationWithNoCountry() throws Exception {
		Location basisLocation = createLocation("Jinja");
		dfs.grantAccess(new Patient(2), basisLocation);
		LocationCohortDefinition def = new LocationCohortDefinition();
		def.setCountry(true);
		def.setLocations(asList(createLocation("Kenya")));
		
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
	}
	
	@Test
	public void evaluate_shouldIgnoreAnEntityBasisMapWhereTheLocationIdHasNoMatchingLocation() throws Exception {
		final Integer basisLocationId = 9999;
		assertNull(ls.getLocation(basisLocationId));
		dfs.grantAccess(new Patient(2), new Location(basisLocationId));
		LocationCohortDefinition def = new LocationCohortDefinition();
		def.setCountry(true);
		def.setLocations(asList(createLocation("Kenya")));
		
		EvaluatedCohort evaluatedCohort = cohortDefService.evaluate(def, new EvaluationContext());
		
		assertEquals(2, evaluatedCohort.activeMembershipSize());
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(7)));
		assertNotNull(evaluatedCohort.getActiveMembership(new Patient(432)));
	}
	
}
