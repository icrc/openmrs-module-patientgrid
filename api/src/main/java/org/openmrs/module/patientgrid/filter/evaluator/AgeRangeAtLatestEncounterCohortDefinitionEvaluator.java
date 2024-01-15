package org.openmrs.module.patientgrid.filter.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.definition.AgeAtLatestEncounterPatientDataDefinition;
import org.openmrs.module.patientgrid.filter.definition.AgeRangeAtLatestEncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Handler(supports = AgeRangeAtLatestEncounterCohortDefinition.class, order = 50)
public class AgeRangeAtLatestEncounterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

  /**
   * Will use a PatientData evaluator
   * {@link org.openmrs.module.patientgrid.evaluator.AgeAtLatestEncounterPatientDataEvaluator} via
   * {@link AgeAtLatestEncounterPatientDataDefinition} to filter the patients ( based on their age and
   * if they have an encounter)
   *
   * @param cohortDefinition  definition to evaluate
   * @param evaluationContext context to use during evaluation
   * @return
   * @throws EvaluationException
   */
  @Override
  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
      throws EvaluationException {

    AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) cohortDefinition;
    AgeAtLatestEncounterPatientDataDefinition ageDef = new AgeAtLatestEncounterPatientDataDefinition();
    ageDef.setEncounterType(def.getEncounterType());
    ageDef.setPeriodRange(def.getPeriodRange());
    ageDef.setLocationCohortDefinition(def.getLocationCohortDefinition());
    EvaluatedPatientData data = Context.getService(PatientDataService.class).evaluate(ageDef, evaluationContext);
    Map<Integer, Age> patientAndAge = (Map) data.getData();
    Set<Integer> patientIds;
    if (def.isAllAgesAccepted()) {
      patientIds = new HashSet<>(patientAndAge.keySet());
    } else {
      patientIds = patientAndAge.entrySet().stream().filter(entry -> {
        for (AgeRange ageRange : def.getAgeRanges()) {
          if (entry.getValue() != null && ageRange.isInRange(entry.getValue())) {
            return true;
          }
        }

        return false;
      }).map(Map.Entry::getKey).collect(Collectors.toSet());
    }
    return new EvaluatedCohort(new Cohort(patientIds), cohortDefinition, evaluationContext);
  }

}
