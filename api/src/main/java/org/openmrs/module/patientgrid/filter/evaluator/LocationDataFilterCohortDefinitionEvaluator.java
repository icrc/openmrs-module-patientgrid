package org.openmrs.module.patientgrid.filter.evaluator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
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
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.datafilter.impl.EntityBasisMap;
import org.openmrs.module.patientgrid.filter.definition.LocationCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = LocationCohortDefinition.class, order = 50)
public class LocationDataFilterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private static final Logger log = LoggerFactory.getLogger(LocationDataFilterCohortDefinitionEvaluator.class);

	private SessionFactory sf;

	@Autowired
	public LocationDataFilterCohortDefinitionEvaluator(SessionFactory sf) {
		this.sf = sf;
	}

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext evaluationContext)
	        throws EvaluationException {

		LocationCohortDefinition locationDef = (LocationCohortDefinition) cohortDefinition;
		Criteria criteria = sf.getCurrentSession().createCriteria(EntityBasisMap.class);
		criteria.add(Restrictions.eq("entityType", Patient.class.getName()));
		criteria.add(Restrictions.eq("basisType", Location.class.getName()));

		Set<Integer> patientIds;
		if (!locationDef.getCountry()) {
			criteria.setProjection(Projections.property("entityIdentifier"));
			List<String> ids = locationDef.getLocations().stream().map(l -> l.getId().toString()).collect(toList());
			criteria.add(Restrictions.in("basisIdentifier", ids));
			List<String> idsAsStrings = criteria.list();
			patientIds = idsAsStrings.stream().map(Integer::valueOf).collect(toSet());
		} else {
			List<EntityBasisMap> entityBasisMaps = criteria.list();
			Set<String> locationNames = locationDef.getLocations().stream().map(l -> l.getName().toLowerCase())
			        .collect(toSet());
			patientIds = new HashSet(entityBasisMaps.size());
			LocationService locationService = Context.getLocationService();
			for (EntityBasisMap map : entityBasisMaps) {
				final Integer locationId = Integer.valueOf(map.getBasisIdentifier());
				Location location = locationService.getLocation(locationId);
				if (location == null) {
					log.warn("No location found with id: {}", locationId);
					continue;
				}

				if (location.getCountry() == null || !locationNames.contains(location.getCountry().toLowerCase())) {
					continue;
				}

				patientIds.add(Integer.valueOf(map.getEntityIdentifier()));
			}
		}

		return new EvaluatedCohort(new Cohort(patientIds), cohortDefinition, evaluationContext);
	}

}
