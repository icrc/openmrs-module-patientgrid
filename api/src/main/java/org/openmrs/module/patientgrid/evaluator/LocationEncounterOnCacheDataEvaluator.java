package org.openmrs.module.patientgrid.evaluator;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.*;
import org.openmrs.annotation.Handler;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.definition.LocationEncounterDataDefinition;
import org.openmrs.module.patientgrid.function.MostRecentEncounterPerPatientByTypeFunction;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Will give the location of the most recent encounter for all patients in the cohort
 */
@Handler(supports = LocationEncounterDataDefinition.class, order = 50)
public class LocationEncounterOnCacheDataEvaluator implements PatientDataEvaluator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationEncounterOnCacheDataEvaluator.class);
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		Cohort baseCohort = context.getBaseCohort();
		Map<Integer, Object> patientIdAndLocationMap = new HashMap(baseCohort.size());
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) context;
		List<Map> allCacheData = contextPersistantCache.getAllCacheData(MostRecentEncounterPerPatientByTypeFunction.class);
		for (CohortMembership member : baseCohort.getMemberships()) {
			Integer patientId = member.getPatientId();
			if (!patientIdAndLocationMap.containsKey(patientId)) {
				Location location = findLocation(allCacheData, patientId);
				if (location == null) {
					LOGGER.warn("No location found for patient {} in the cache", patientId);
				} else {
					patientIdAndLocationMap.put(patientId, location);
				}
			}
		}
		EvaluatedPatientData data = new EvaluatedPatientData(definition, context);
		data.setData(patientIdAndLocationMap);
		return data;
	}
	
	private Location findLocation(List<Map> allCacheData, Integer patientId) {
		Encounter encounter = null;
		for (Map map : allCacheData) {
			Object o = map.get(patientId);
			Encounter currentEncounter = null;
			if (o instanceof Encounter) {
				currentEncounter = (Encounter) o;
			} else {
				List encounterList = (List) o;
				if (CollectionUtils.isNotEmpty(encounterList)) {
					currentEncounter = (Encounter) encounterList.get(0);
				}
			}
			if (currentEncounter != null) {
				if (encounter == null || currentEncounter.getEncounterDatetime().after(encounter.getEncounterDatetime())) {
					encounter = currentEncounter;
				}
			}
		}
		return encounter == null ? null : encounter.getLocation();
	}
	
}
