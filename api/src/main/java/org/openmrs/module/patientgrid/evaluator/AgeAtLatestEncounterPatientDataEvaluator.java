package org.openmrs.module.patientgrid.evaluator;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
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

import java.util.HashMap;
import java.util.Map;

@Handler(supports = AgeAtLatestEncounterPatientDataDefinition.class, order = 50)
public class AgeAtLatestEncounterPatientDataEvaluator implements PatientDataEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(AgeAtLatestEncounterPatientDataEvaluator.class);
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		AgeAtLatestEncounterPatientDataDefinition def = (AgeAtLatestEncounterPatientDataDefinition) definition;
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) context;
		
		MostRecentEncounterPerPatientByTypeFunction encounterFct = new MostRecentEncounterPerPatientByTypeFunction(
		        contextPersistantCache, def.getPeriodRange());
		//will retrieve the map patientid-> Ecounter if not in cache
		Map<Integer, Object> patientIdAndEnc = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    encounterFct);
		
		PatientAgePerEncounterIdByTypeFunction agePerEncounterIdFct = new PatientAgePerEncounterIdByTypeFunction(context,
		        patientIdAndEnc);
		//will retrieve the map encounter id -> age if not in cache
		Map<Integer, Object> encIdAndAge = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    agePerEncounterIdFct);
		
		//transform these 2 maps in patient id -> age
		Map<Integer, Object> patientIdAndAge = new HashMap<>();
		patientIdAndEnc.entrySet().forEach(e -> {
			Integer patientId = e.getKey();
			Encounter en = (Encounter) e.getValue();
			patientIdAndAge.put(patientId, encIdAndAge.get(en.getId()));
		});
		EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
		result.setData(patientIdAndAge);
		
		return result;
	}
	
}
