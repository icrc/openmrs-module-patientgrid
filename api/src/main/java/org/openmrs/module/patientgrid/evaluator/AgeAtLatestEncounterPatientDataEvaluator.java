package org.openmrs.module.patientgrid.evaluator;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.definition.AgeAtLatestEncounterPatientDataDefinition;
import org.openmrs.module.patientgrid.function.MostRecentEncounterPerPatientByTypeFunction;
import org.openmrs.module.patientgrid.function.PatientAgePerEncounterIdByTypeFunction;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Handler(supports = AgeAtLatestEncounterPatientDataDefinition.class, order = 50)
public class AgeAtLatestEncounterPatientDataEvaluator implements PatientDataEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(AgeAtLatestEncounterPatientDataEvaluator.class);
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		AgeAtLatestEncounterPatientDataDefinition def = (AgeAtLatestEncounterPatientDataDefinition) definition;
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) context;
		
		MostRecentEncounterPerPatientByTypeFunction encounterFct = new MostRecentEncounterPerPatientByTypeFunction(
		        contextPersistantCache);
		Map<Integer, Object> patientIdAndEnc = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    encounterFct);
		
		PatientAgePerEncounterIdByTypeFunction agePerEncounterIdFct = new PatientAgePerEncounterIdByTypeFunction(context,
		        patientIdAndEnc);
		Map<Integer, Object> encIdAndAge = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    agePerEncounterIdFct);
		
		EncounterService es = Context.getEncounterService();
		Map<Integer, Object> patientIdAndAge = new HashMap(encIdAndAge.size());
		for (Map.Entry<Integer, Object> e : encIdAndAge.entrySet()) {
			patientIdAndAge.put(es.getEncounter(e.getKey()).getPatient().getId(), e.getValue());
		}
		
		EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
		result.setData(patientIdAndAge);
		
		return result;
	}
	
}
