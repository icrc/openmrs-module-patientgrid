package org.openmrs.module.patientgrid.evaluator;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.definition.ObsForLatestEncounterPatientDataDefinition;
import org.openmrs.module.patientgrid.function.MostRecentEncounterPerPatientByTypeFunction;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Handler(supports = ObsForLatestEncounterPatientDataDefinition.class, order = 50)
public class ObsForLatestEncounterPatientDataEvaluator implements PatientDataEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(ObsForLatestEncounterPatientDataEvaluator.class);
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		ObsForLatestEncounterPatientDataDefinition def = (ObsForLatestEncounterPatientDataDefinition) definition;
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) context;
		Map<Integer, Object> patientIdAndEnc = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    new MostRecentEncounterPerPatientByTypeFunction(contextPersistantCache, def.getPeriodRange()));
		
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
