package org.openmrs.module.patientgrid;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = PatientLocationDataDefinition.class, order = 50)
public class PatientLocationDataEvaluator implements PatientDataEvaluator {
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		EvaluatedPatientData data = new EvaluatedPatientData(definition, context);
		data.setData(PatientLocationEvaluatorUtils.evaluate(definition, context));
		
		return data;
	}
	
}
