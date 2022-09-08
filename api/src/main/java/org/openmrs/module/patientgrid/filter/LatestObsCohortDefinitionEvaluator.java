package org.openmrs.module.patientgrid.filter;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = LatestObsCohortDefinition.class, order = 50)
public class LatestObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	//Unfortunately hibernate's criteria API and HQL don't support joins to a derived table with multiple columns
	private final static String MOST_RECENT_DATES = "SELECT e.patient_id, max(e.encounter_datetime) AS maxDate FROM encounter e, "
	        + "patient p WHERE e.patient_id = p.patient_id AND e.encounter_type = :encType AND e.voided = false AND"
	        + " p.voided = false GROUP BY e.patient_id";
	
	private final static String MOST_RECENT_ENC_IDS = "SELECT e1.encounter_id FROM encounter e1 INNER JOIN ("
	        + MOST_RECENT_DATES + ") e2 ON e1.patient_id = e2.patient_id AND e1.encounter_datetime = e2.maxDate";
	
	private SessionFactory sf;
	
	@Autowired
	public LatestObsCohortDefinitionEvaluator(SessionFactory sf) {
		this.sf = sf;
	}
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
	    throws EvaluationException {
		
		LatestObsCohortDefinition cohortDef = (LatestObsCohortDefinition) cohortDefinition;
		Query query = sf.getCurrentSession().createSQLQuery(MOST_RECENT_ENC_IDS);
		query.setParameter("encType", cohortDef.getEncounterType());
		List<Integer> encounterIds = query.list();
		
		Criteria criteria = sf.getCurrentSession().createCriteria(Obs.class, "o");
		criteria.createCriteria("person", "p");
		criteria.setProjection(Projections.property("p.personId"));
		criteria.add(Restrictions.eq("o.concept", cohortDef.getConcept()));
		criteria.add(Restrictions.eq("o.voided", false));
		criteria.add(Restrictions.in("o." + cohortDef.getPropertyName(), cohortDef.getValues()));
		
		criteria.createCriteria("encounter", "e");
		criteria.add(Restrictions.in("e.encounterId", encounterIds));
		
		return new EvaluatedCohort(new Cohort(criteria.list()), cohortDefinition, evaluationContext);
	}
	
}
