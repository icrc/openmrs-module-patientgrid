package org.openmrs.module.patientgrid.evaluator;

import org.openmrs.*;
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
import java.util.Set;

@Handler(supports = ObsForLatestEncounterPatientDataDefinition.class, order = 50)
public class ObsForLatestEncounterPatientDataEvaluator implements PatientDataEvaluator {
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		Cohort baseCohort = context.getBaseCohort();
		if (baseCohort != null && baseCohort.isEmpty()) {
			new EvaluatedPatientData(definition, context);
		}
		ObsForLatestEncounterPatientDataDefinition def = (ObsForLatestEncounterPatientDataDefinition) definition;
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) context;
		Map<Integer, Object> patientIdAndEnc = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    new MostRecentEncounterPerPatientByTypeFunction(contextPersistantCache, def.getPeriodRange(),
		            def.getLocationCohortDefinition()));
		
		Map<Integer, Object> patientIdAndObs = new HashMap(patientIdAndEnc.size());
		Set<Integer> patients = baseCohort == null ? patientIdAndEnc.keySet() : baseCohort.getMemberIds();
		for (Integer patientId : patients) {
			Encounter e = (Encounter) patientIdAndEnc.get(patientId);
			Obs obs = PatientGridUtils.getObsByConcept(e, def.getConcept());
			if (obs != null) {
				patientIdAndObs.put(patientId, obs);
			}
		}
		
		EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
		result.setData(patientIdAndObs);
		
		return result;
	}
	
}
