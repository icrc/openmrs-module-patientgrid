package org.openmrs.module.patientgrid.evaluator;

import static org.openmrs.module.patientgrid.PatientGridConstants.KEY_MOST_RECENT_ENCS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.definition.DateForLatestEncounterPatientDataDefinition;
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
		//TODO Move this code to a base class
		Map<EncounterType, Map> typeAndEncData = (Map) context.getFromCache(KEY_MOST_RECENT_ENCS);
		if (typeAndEncData == null) {
			typeAndEncData = new HashMap();
			context.addToCache(KEY_MOST_RECENT_ENCS, typeAndEncData);
		}
		
		Map<Integer, Object> patientIdAndEnc = typeAndEncData.get(def.getEncounterType());
		if (patientIdAndEnc == null) {
			if (log.isDebugEnabled()) {
				log.debug("Loading patient most recent patient encounters of type: " + def.getEncounterType());
			}
			
			patientIdAndEnc = PatientGridUtils.getEncounters(def.getEncounterType(), context, true);
			typeAndEncData.put(def.getEncounterType(), patientIdAndEnc);
		}
		
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
