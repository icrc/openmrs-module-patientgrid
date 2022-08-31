package org.openmrs.module.patientgrid;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.datafilter.impl.EntityBasisMap;
import org.openmrs.module.datafilter.impl.api.DataFilterService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientLocationEvaluatorUtilsTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private DataFilterService dataFilterService;
	
	@Test
	public void evaluate_shouldReturnThePatientLocation() throws Exception {
		executeDataSet("entityBasisMaps.xml");
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(asList(patientId2, patientId6)));
		
		Map<Integer, Object> idAndLocationMap = PatientLocationEvaluatorUtils.evaluate(new PatientLocationDataDefinition(),
		    context);
		
		assertEquals(2, idAndLocationMap.size());
		assertEquals(locationService.getLocation(4000), idAndLocationMap.get(patientId2));
		assertEquals(locationService.getLocation(4001), idAndLocationMap.get(patientId6));
	}
	
	@Test
	public void evaluate_shouldReturnNullForAPatientWithNoMappedLocation() throws Exception {
		executeDataSet("entityBasisMaps.xml");
		final Integer patientId = 999;
		Patient patient = patientService.getPatient(patientId);
		assertNotNull(patient);
		assertTrue(dataFilterService.getEntityBasisMaps(patient, Location.class.getName()).isEmpty());
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(asList(patientId)));
		assertTrue(PatientLocationEvaluatorUtils.evaluate(new PatientLocationDataDefinition(), context).isEmpty());
	}
	
	@Test
	public void evaluate_shouldReturnTheLocationAssociatedToTheMostRecentMappingInCaseOfMoreThanOne() throws Exception {
		executeDataSet("entityBasisMaps.xml");
		final Integer patientId = 7;
		Patient patient = patientService.getPatient(patientId);
		List<EntityBasisMap> maps = (List) dataFilterService.getEntityBasisMaps(patient, Location.class.getName());
		assertEquals(3, maps.size());
		assertTrue(maps.get(0).getDateCreated().before(maps.get(1).getDateCreated()));
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(asList(patientId)));
		PatientLocationDataDefinition d = new PatientLocationDataDefinition();
		
		Map<Integer, Object> idAndLocationMap = PatientLocationEvaluatorUtils.evaluate(d, context);
		
		assertEquals(1, idAndLocationMap.size());
		assertEquals(locationService.getLocation(4001), idAndLocationMap.get(patientId));
	}
	
	@Test
	public void evaluate_shouldReturnTheLocationAssociatedToTheLastAddedMappingInCaseOfMoreThanOneWithSameDateCreated()
	        throws Exception {
		executeDataSet("entityBasisMaps.xml");
		final Integer patientId = 432;
		Patient patient = patientService.getPatient(patientId);
		List<EntityBasisMap> maps = (List) dataFilterService.getEntityBasisMaps(patient, Location.class.getName());
		assertEquals(2, maps.size());
		assertEquals(maps.get(0).getDateCreated(), (maps.get(1).getDateCreated()));
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(asList(patientId)));
		
		Map<Integer, Object> idAndLocationMap = PatientLocationEvaluatorUtils.evaluate(new PatientLocationDataDefinition(),
		    context);
		
		assertEquals(1, idAndLocationMap.size());
		assertEquals(locationService.getLocation(4002), idAndLocationMap.get(patientId));
	}
	
}
