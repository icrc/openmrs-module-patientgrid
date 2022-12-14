package org.openmrs.module.patientgrid.evaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.definition.DateForLatestEncounterPatientDataDefinition;
import org.openmrs.module.patientgrid.function.MostRecentEncounterPerPatientByTypeFunction;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Handler(supports = DateForLatestEncounterPatientDataDefinition.class, order = 50)
public class DateForLatestEncounterPatientDataEvaluator implements PatientDataEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(DateForLatestEncounterPatientDataEvaluator.class);
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		DateForLatestEncounterPatientDataDefinition def = (DateForLatestEncounterPatientDataDefinition) definition;
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) context;
		Map<Integer, Object> patientIdAndEnc = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    new MostRecentEncounterPerPatientByTypeFunction(contextPersistantCache));
		
		Map<Integer, Object> encIdAndEnc = patientIdAndEnc.values().stream()
		        .collect(Collectors.toMap(e -> ((Encounter) e).getId(), Function.identity()));
		
		EncounterIdSet encIdSet = new EncounterIdSet(encIdAndEnc.keySet());
		EncounterEvaluationContext encContext = new EncounterEvaluationContext(context, encIdSet);
		Map<Integer, Object> encIdAndDate = Context.getService(EncounterDataService.class)
		        .evaluate(new EncounterDatetimeDataDefinition(), encContext).getData();
		
		Map<Integer, Object> patientIdAndEncDate = new HashMap(encIdAndDate.size());
		for (Map.Entry<Integer, Object> e : encIdAndDate.entrySet()) {
			patientIdAndEncDate.put(((Encounter) encIdAndEnc.get(e.getKey())).getPatient().getId(), e.getValue());
		}
		
		EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
		result.setData(patientIdAndEncDate);
		
		return result;
	}
	
}
