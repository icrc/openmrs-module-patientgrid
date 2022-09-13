package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridConstants.KEY_AGES_AT_ENCS;
import static org.openmrs.module.patientgrid.PatientGridConstants.KEY_MOST_RECENT_ENCS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
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

@Handler(supports = PatientAgeAtLatestEncounterDataDefinition.class, order = 50)
public class PatientAgeAtLatestEncounterDataEvaluator implements PatientDataEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(PatientAgeAtLatestEncounterDataEvaluator.class);
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		PatientAgeAtLatestEncounterDataDefinition def = (PatientAgeAtLatestEncounterDataDefinition) definition;
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
			
			patientIdAndEnc = PatientGridUtils.getMostRecentEncounters(def.getEncounterType(), context.getBaseCohort(),
			    true);
			typeAndEncData.put(def.getEncounterType(), patientIdAndEnc);
		}
		
		Map<EncounterType, Map> typeAndAgesData = (Map) context.getFromCache(KEY_AGES_AT_ENCS);
		if (typeAndAgesData == null) {
			typeAndAgesData = new HashMap();
			context.addToCache(KEY_AGES_AT_ENCS, typeAndAgesData);
		}
		
		Map<Integer, Object> encIdAndAge = typeAndAgesData.get(def.getEncounterType());
		if (encIdAndAge == null) {
			if (log.isDebugEnabled()) {
				log.debug("Loading patient ages at most recent encounters of type: " + def.getEncounterType());
			}
			
			List<Integer> encIds = patientIdAndEnc.values().stream().map(e -> ((Encounter) e).getId())
			        .collect(Collectors.toList());
			
			EncounterIdSet encIdSet = new EncounterIdSet(encIds);
			EncounterEvaluationContext encContext = new EncounterEvaluationContext(context, encIdSet);
			encIdAndAge = Context.getService(EncounterDataService.class)
			        .evaluate(new AgeAtEncounterDataDefinition(), encContext).getData();
			typeAndEncData.put(def.getEncounterType(), encIdAndAge);
		}
		
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
