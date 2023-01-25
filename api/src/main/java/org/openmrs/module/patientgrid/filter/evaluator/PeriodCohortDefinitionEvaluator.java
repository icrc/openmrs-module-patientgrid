package org.openmrs.module.patientgrid.filter.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.definition.DateForLatestEncounterPatientDataDefinition;
import org.openmrs.module.patientgrid.filter.definition.PeriodCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Handler(supports = PeriodCohortDefinition.class, order = 50)
public class PeriodCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
            throws EvaluationException {
        PeriodCohortDefinition def = (PeriodCohortDefinition) cohortDefinition;
        DateForLatestEncounterPatientDataDefinition periodDef = new DateForLatestEncounterPatientDataDefinition();
        periodDef.setEncounterType(def.getEncounterType());
        EvaluatedPatientData data = Context.getService(PatientDataService.class).evaluate(periodDef, evaluationContext);
        Map<Integer, Date> patientAndDate = (Map) data.getData();
        Set<Integer> patientIds;
        patientIds = patientAndDate.entrySet().stream().filter(entry -> {
            if (!def.getFromDate().after(entry.getValue()) && !def.getToDate().before(entry.getValue())) {
                return true;
            }
            return false;
        }).map(Map.Entry::getKey).collect(Collectors.toSet());
        return new EvaluatedCohort(new Cohort(patientIds), cohortDefinition, evaluationContext);
    }
}
