package org.openmrs.module.patientgrid.function;

import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.PeriodRange;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class MostRecentEncounterPerPatientByTypeFunction implements Function<EncounterType, Map> {
	
	private final EvaluationContext context;
	
	private final PeriodRange periodRange;
	
	private static final Logger log = LoggerFactory.getLogger(MostRecentEncounterPerPatientByTypeFunction.class);
	
	public MostRecentEncounterPerPatientByTypeFunction(EvaluationContext context, PeriodRange periodRange) {
		this.context = context;
		this.periodRange = periodRange;
	}
	
	@Override
	public Map apply(EncounterType encounterType) {
		log.debug("Loading patient most recent patient encounters of type: {}", encounterType);
		try {
			return PatientGridUtils.getEncounters(encounterType, context, true, periodRange);
		}
		catch (EvaluationException e) {
			throw new RuntimeException(e);
		}
	}
}
