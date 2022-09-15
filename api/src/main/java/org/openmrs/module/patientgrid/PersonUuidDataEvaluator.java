package org.openmrs.module.patientgrid;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.evaluator.PersonPropertyDataEvaluator;

/**
 * Evaluates a PatientUuidDataDefinition to produce a PersonData
 */
@Handler(supports = PersonUuidDataDefinition.class, order = 50)
public class PersonUuidDataEvaluator extends PersonPropertyDataEvaluator {
	
	@Override
	public String getPropertyName() {
		return "uuid";
	}
	
}
