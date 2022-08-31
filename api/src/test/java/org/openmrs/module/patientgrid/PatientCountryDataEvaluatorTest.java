package org.openmrs.module.patientgrid;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.LocationService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientCountryDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientDataService patientDataService;
	
	@Autowired
	private LocationService locationService;
	
	@Test
	public void evaluate_shouldReturnThePatientLocation() throws Exception {
		executeDataSet("entityBasisMaps.xml");
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(asList(patientId2, patientId6)));
		
		EvaluatedPatientData data = patientDataService.evaluate(new PatientCountryDataDefinition(), context);
		
		assertEquals(2, data.getData().size());
		assertEquals(locationService.getLocation(4000).getCountry(), data.getData().get(patientId2));
		assertEquals(locationService.getLocation(4001).getCountry(), data.getData().get(patientId6));
	}
	
}
