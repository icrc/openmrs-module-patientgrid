package org.openmrs.module.patientgrid.download;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.ObsPatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.*;
import java.util.stream.Collectors;

import static org.openmrs.module.patientgrid.PatientGridConstants.OBS_CONVERTER;

@Handler(supports = AllEncountersPatientDataDefinition.class, order = 50)
public class AllEncountersPatientDataEvaluator implements PatientDataEvaluator {
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		AllEncountersPatientDataDefinition def = (AllEncountersPatientDataDefinition) definition;
		Map<Integer, Object> patientIdAndEncs = PatientGridUtils.getEncounters(def.getEncounterType(),
		    (EvaluationContextPersistantCache) context, def.getLocationCohortDefinition(), false, def.getPeriodRange());
		Set<ObsPatientGridColumn> obsColumns = def.getPatientGrid().getObsColumns();
		
		Map<Integer, Object> patientIdAndEncList = patientIdAndEncs.entrySet().stream()
		        .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
			        List<Map<String, Object>> encounters = new ArrayList(patientIdAndEncs.size());
			        List<Encounter> patientEncs = (List) entry.getValue();
			        patientEncs.stream().forEach(encounter -> {
				        Map<String, Object> columnUuidAndObsMap = new HashMap(obsColumns.size());
				        obsColumns.stream().forEach(column -> {
					        Obs obs = PatientGridUtils.getObsByConcept(encounter, column.getConcept());
					        if (obs != null) {
						        columnUuidAndObsMap.put(column.getUuid(), DataUtil.convertData(obs, OBS_CONVERTER));
					        }
				        });
				        
				        encounters.add(columnUuidAndObsMap);
			        });
			        
			        return encounters;
		        }));
		
		EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
		result.setData(patientIdAndEncList);
		
		return result;
	}
	
}
