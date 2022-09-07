package org.openmrs.module.patientgrid.filter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = LatestObsCohortDefinition.class, order = 50)
public class LatestObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	@Autowired
	private SessionFactory sf;
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
	    throws EvaluationException {
		
		//TODO use a sub query to fetch only personIds and move voided restriction
		LatestObsCohortDefinition cohortDef = (LatestObsCohortDefinition) cohortDefinition;
		Criteria criteria = sf.getCurrentSession().createCriteria(Obs.class, "o");
		criteria.add(Restrictions.eq("o.concept", cohortDef.getConcept()));
		criteria.add(Restrictions.eq("o.voided", false));
		criteria.add(Restrictions.in("o." + cohortDef.getPropertyName(), cohortDef.getValues()));
		ProjectionList pl = Projections.projectionList();
		pl.add(Projections.groupProperty("person"));
		pl.add(Projections.max("obsDatetime"), "maxObsDatetime");
		criteria.setProjection(pl);
		
		criteria.createCriteria("person", "p");
		criteria.add(Restrictions.eq("p.voided", false));
		
		criteria.createCriteria("encounter", "e");
		criteria.add(Restrictions.isNotNull("o.encounter"));
		criteria.add(Restrictions.eq("e.encounterType", cohortDef.getEncounterType()));
		criteria.add(Restrictions.eq("e.voided", false));
		
		List<Object[]> resultRows = criteria.list();
		List<Integer> patientIds = new ArrayList(resultRows.size());
		resultRows.stream().forEach(row -> patientIds.add(((Person) row[0]).getId()));
		
		return new EvaluatedCohort(new Cohort(patientIds), cohortDefinition, evaluationContext);
	}
	
}
