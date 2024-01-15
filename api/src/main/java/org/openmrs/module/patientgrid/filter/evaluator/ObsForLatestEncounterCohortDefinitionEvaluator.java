package org.openmrs.module.patientgrid.filter.evaluator;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.filter.definition.ObsForLatestEncounterCohortDefinition;
import org.openmrs.module.patientgrid.function.MostRecentEncounterIdByTypeFunction;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = ObsForLatestEncounterCohortDefinition.class, order = 50)
public class ObsForLatestEncounterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	//Unfortunately hibernate's criteria API and HQL don't support joins to a derived table with multiple columns

	private SessionFactory sf;

	@Autowired
	public ObsForLatestEncounterCohortDefinitionEvaluator(SessionFactory sf) {
		this.sf = sf;
	}

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
	        throws EvaluationException {

		ObsForLatestEncounterCohortDefinition cohortDef = (ObsForLatestEncounterCohortDefinition) cohortDefinition;
		EvaluationContextPersistantCache contextPersistantCache = (EvaluationContextPersistantCache) evaluationContext;

		MostRecentEncounterIdByTypeFunction function = new MostRecentEncounterIdByTypeFunction(sf,
		        cohortDef.getPeriodRange());
		List<Integer> encounterIds = contextPersistantCache.computeListIfAbsent(cohortDef.getEncounterType(), function);

		Criteria criteria = sf.getCurrentSession().createCriteria(Obs.class, "o");
		criteria.createCriteria("person", "p");
		criteria.setProjection(Projections.property("p.personId"));
		criteria.add(Restrictions.eq("o.concept", cohortDef.getConcept()));
		criteria.add(Restrictions.eq("o.voided", false));
		//TODO value text should be case insensitive
		criteria.add(Restrictions.in("o." + cohortDef.getPropertyName(), cohortDef.getValues()));

		criteria.createCriteria("encounter", "e");
		criteria.add(Restrictions.in("e.encounterId", encounterIds));

		return new EvaluatedCohort(new Cohort(criteria.list()), cohortDefinition, evaluationContext);
	}

}
