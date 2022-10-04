package org.openmrs.module.patientgrid.evaluator;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.PersonService;
import org.openmrs.module.patientgrid.definition.PersonUuidDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PersonUuidDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private PersonDataService personDataService;
	
	@Autowired
	@Qualifier("personService")
	private PersonService personService;
	
	@Test
	public void evaluate_shouldReturnThePatientUuid() throws Exception {
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(asList(patientId2, patientId6)));
		
		EvaluatedPersonData data = personDataService.evaluate(new PersonUuidDataDefinition(), context);
		
		assertEquals(2, data.getData().size());
		assertEquals(personService.getPerson(patientId2).getUuid(), data.getData().get(patientId2));
		assertEquals(personService.getPerson(patientId6).getUuid(), data.getData().get(patientId6));
	}
	
}
