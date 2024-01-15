package org.openmrs.module.patientgrid.download;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.patientgrid.EncounterDatePatientGridColumn;
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
    Cohort baseCohort = context.getBaseCohort();
    if (baseCohort != null && baseCohort.isEmpty()) {
      new EvaluatedPatientData(definition, context);
    }
    AllEncountersPatientDataDefinition def = (AllEncountersPatientDataDefinition) definition;
    Map<Integer, Object> patientIdAndEncs = PatientGridUtils.getEncounters(def.getEncounterType(),
        (EvaluationContextPersistantCache) context, def.getLocationCohortDefinition(), false, def.getPeriodRange());
    Set<ObsPatientGridColumn> obsColumns = def.getPatientGrid().getObsColumns();

    Set<Integer> patients = baseCohort == null ? patientIdAndEncs.keySet() : baseCohort.getMemberIds();
    Map<Integer, Object> patientIdAndEncList = patients.stream()
        .collect(Collectors.toMap(patientId -> patientId, patientId -> {
          List<Map<String, Object>> encounters = new ArrayList(patientIdAndEncs.size());
          List<Encounter> patientEncs = (List) patientIdAndEncs.get(patientId);
          patientEncs.stream().forEach(encounter -> {
            Map<String, Object> columnUuidAndObsMap = new HashMap(obsColumns.size());
            obsColumns.stream().forEach(column -> {
              Obs obs = PatientGridUtils.getObsByConcept(encounter, column.getConcept());
              if (obs != null) {
                columnUuidAndObsMap.put(column.getUuid(), DataUtil.convertData(obs, OBS_CONVERTER));
              }
            });
            EncounterDatePatientGridColumn dateColumn = def.getPatientGrid()
                .getDateColumn(def.getEncounterType());

            if (dateColumn != null) {
              columnUuidAndObsMap.put(dateColumn.getName(), encounter.getEncounterDatetime());
            }
            encounters.add(columnUuidAndObsMap);

          });

          return encounters;
        }));

    EvaluatedPatientData result = new EvaluatedPatientData(definition, context);
    result.setData(patientIdAndEncList);

    return result;
  }

}
