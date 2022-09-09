package org.openmrs.module.patientgrid.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = ObsForLatestEncounterCohortDefinition.class, order = 50)
public class ObsForLatestEncounterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(ObsForLatestEncounterCohortDefinitionEvaluator.class);
	
	//Unfortunately hibernate's criteria API and HQL don't support joins to a derived table with multiple columns
	private final static String MOST_RECENT_DATES = "SELECT e.patient_id, max(e.encounter_datetime) AS maxDate FROM encounter e, "
	        + "patient p WHERE e.patient_id = p.patient_id AND e.encounter_type = :encType AND e.voided = false AND"
	        + " p.voided = false GROUP BY e.patient_id";
	
	private final static String MOST_RECENT_ENC_IDS = "SELECT e1.encounter_id FROM encounter e1 INNER JOIN ("
	        + MOST_RECENT_DATES + ") e2 ON e1.patient_id = e2.patient_id AND e1.encounter_datetime = e2.maxDate";
	
	protected static final String KEY_MOST_RECENT_ENC_IDS = PatientGridConstants.class.getName() + "_AGES";
	
	private SessionFactory sf;
	
	@Autowired
	public ObsForLatestEncounterCohortDefinitionEvaluator(SessionFactory sf) {
		this.sf = sf;
	}
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
	    throws EvaluationException {
		
		ObsForLatestEncounterCohortDefinition cohortDef = (ObsForLatestEncounterCohortDefinition) cohortDefinition;
		Map<EncounterType, List<Integer>> typeAndEncIds = (Map) evaluationContext.getFromCache(KEY_MOST_RECENT_ENC_IDS);
		if (typeAndEncIds == null) {
			typeAndEncIds = new HashMap();
			evaluationContext.addToCache(KEY_MOST_RECENT_ENC_IDS, typeAndEncIds);
		}
		
		List<Integer> encounterIds = typeAndEncIds.get(cohortDef.getEncounterType());
		if (encounterIds == null) {
			if (log.isDebugEnabled()) {
				log.debug("Loading ids for the latest patient encounters of type: " + cohortDef.getEncounterType());
			}
			
			Query query = sf.getCurrentSession().createSQLQuery(MOST_RECENT_ENC_IDS);
			query.setParameter("encType", cohortDef.getEncounterType());
			encounterIds = query.list();
			typeAndEncIds.put(cohortDef.getEncounterType(), encounterIds);
		}
		
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
