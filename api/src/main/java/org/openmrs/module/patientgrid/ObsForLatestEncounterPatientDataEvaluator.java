package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridConstants.KEY_MOST_RECENT_ENCS;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Handler(supports = ObsForLatestEncounterPatientDataDefinition.class, order = 50)
public class ObsForLatestEncounterPatientDataEvaluator implements PatientDataEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(ObsForLatestEncounterPatientDataEvaluator.class);
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		ObsForLatestEncounterPatientDataDefinition def = (ObsForLatestEncounterPatientDataDefinition) definition;
		Map<EncounterType, Object> typeAndEncData = (Map) context.getFromCache(KEY_MOST_RECENT_ENCS);
		if (typeAndEncData == null) {
			typeAndEncData = new HashMap();
			context.addToCache(KEY_MOST_RECENT_ENCS, typeAndEncData);
		}
		
		Map<Integer, Object> patientIdAndEnc = (Map) typeAndEncData.get(def.getEncounterType());
		if (patientIdAndEnc == null) {
			if (log.isDebugEnabled()) {
				log.debug("Loading patient most recent patient encounters of type: " + def.getEncounterType());
			}
			
			patientIdAndEnc = PatientGridUtils.getMostRecentEncounters(def.getEncounterType(), context.getBaseCohort(),
			    true);
			typeAndEncData.put(def.getEncounterType(), patientIdAndEnc);
		}
		
		Map<Integer, Object> patientIdAndObs = new HashMap(patientIdAndEnc.size());
		for (Map.Entry<Integer, Object> e : patientIdAndEnc.entrySet()) {
			Obs obs = PatientGridUtils.getObsByConcept((Encounter) e.getValue(), def.getConcept());
			if (obs != null) {
				patientIdAndObs.put(e.getKey(), obs);
			}
		}
		
		EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
		result.setData(patientIdAndObs);
		
		return result;
	}
	
}
