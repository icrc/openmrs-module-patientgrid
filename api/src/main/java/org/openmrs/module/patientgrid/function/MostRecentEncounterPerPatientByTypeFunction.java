package org.openmrs.module.patientgrid.function;

import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.filter.definition.LocationCohortDefinition;
import org.openmrs.module.patientgrid.period.DateRange;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class MostRecentEncounterPerPatientByTypeFunction implements Function<EncounterType, Map> {
	
	private static final Logger log = LoggerFactory.getLogger(MostRecentEncounterPerPatientByTypeFunction.class);
	
	private final EvaluationContextPersistantCache context;
	
	private final DateRange periodRange;
	
	private final LocationCohortDefinition locationCohortDefinition;
	
	public MostRecentEncounterPerPatientByTypeFunction(EvaluationContextPersistantCache context, DateRange periodRange,
	    LocationCohortDefinition locationCohortDefinition) {
		this.context = context;
		this.periodRange = periodRange;
		this.locationCohortDefinition = locationCohortDefinition;
	}
	
	@Override
	public Map apply(EncounterType encounterType) {
		log.debug("Loading patient most recent patient encounters of type: {}", encounterType);
		try {
			return PatientGridUtils.getEncounters(encounterType, context, locationCohortDefinition, true, periodRange);
		}
		catch (EvaluationException e) {
			throw new RuntimeException(e);
		}
	}
}
