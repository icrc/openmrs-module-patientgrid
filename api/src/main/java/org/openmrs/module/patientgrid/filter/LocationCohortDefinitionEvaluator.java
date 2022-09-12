package org.openmrs.module.patientgrid.filter;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.datafilter.impl.EntityBasisMap;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = LocationCohortDefinition.class, order = 50)
public class LocationCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	private SessionFactory sf;
	
	@Autowired
	public LocationCohortDefinitionEvaluator(SessionFactory sf) {
		this.sf = sf;
	}
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
	        throws EvaluationException {
		
		LocationCohortDefinition locationDef = (LocationCohortDefinition) cohortDefinition;
		Criteria criteria = sf.getCurrentSession().createCriteria(EntityBasisMap.class);
		criteria.setProjection(Projections.property("entityIdentifier"));
		criteria.add(Restrictions.eq("entityType", Patient.class.getName()));
		criteria.add(Restrictions.eq("basisType", Location.class.getName()));
		List<String> ids = locationDef.getLocations().stream().map(l -> l.getLocationId().toString()).collect(toList());
		criteria.add(Restrictions.in("basisIdentifier", ids));
		
		List<String> idsAsStrings = criteria.list();
		
		Set<Integer> patientIds = idsAsStrings.stream().map(id -> Integer.valueOf(id)).collect(toSet());
		
		return new EvaluatedCohort(new Cohort(patientIds), cohortDefinition, evaluationContext);
	}
	
}
