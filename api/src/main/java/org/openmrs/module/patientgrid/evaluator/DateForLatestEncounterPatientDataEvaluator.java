package org.openmrs.module.patientgrid.evaluator;

import org.openmrs.Cohort;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Handler(supports = DateForLatestEncounterPatientDataDefinition.class, order = 50)
public class DateForLatestEncounterPatientDataEvaluator implements PatientDataEvaluator {
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		Cohort baseCohort = context.getBaseCohort();
		if (baseCohort != null && baseCohort.isEmpty()) {
			new EvaluatedPatientData(definition, context);
		}
		final Set<Integer> patientIds = baseCohort == null ? null : baseCohort.getMemberIds();
		DateForLatestEncounterPatientDataDefinition def = (DateForLatestEncounterPatientDataDefinition) definition;
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) context;
		Map<Integer, Object> patientIdAndEnc = contextPersistantCache.computeMapIfAbsent(def.getEncounterType(),
		    new MostRecentEncounterPerPatientByTypeFunction(contextPersistantCache, def.getPeriodRange(),
		            def.getLocationCohortDefinition()));
		
		Map<Integer, Object> patientIdAndEncDate = patientIdAndEnc.entrySet().stream()
		        .filter(entry -> patientIds == null || patientIds.contains(entry.getKey())).collect(Collectors
		                .toMap(entry -> entry.getKey(), entry -> ((Encounter) entry.getValue()).getEncounterDatetime()));
		
		EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
		result.setData(patientIdAndEncDate);
		
		return result;
	}
	
}
