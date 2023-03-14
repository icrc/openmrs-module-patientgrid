package org.openmrs.module.patientgrid.function;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.converter.BirthdateToAgeConverter;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PatientAgePerEncounterIdByTypeFunction implements Function<EncounterType, Map> {
	
	private static final Logger log = LoggerFactory.getLogger(PatientAgePerEncounterIdByTypeFunction.class);
	
	private final EvaluationContext context;
	
	private final Map<Integer, Object> patientIdAndEnc;
	
	public PatientAgePerEncounterIdByTypeFunction(EvaluationContext context, Map<Integer, Object> patientIdAndEnc) {
		this.context = context;
		this.patientIdAndEnc = patientIdAndEnc;
	}
	
	/**
	 * @param encounterType the function argument
	 * @return encounterId -> Age
	 */
	@Override
	public Map apply(EncounterType encounterType) {
		
		log.debug("Loading patient ages at most recent encounters of type: {}", encounterType);
		List<Integer> encIds = patientIdAndEnc.values().stream().map(e -> ((Encounter) e).getId())
		        .collect(Collectors.toList());
		
		BirthdateToAgeConverter converter = new BirthdateToAgeConverter();
		Map<Integer, Object> agePerEncounterId = new HashMap<>();
		patientIdAndEnc.values().stream().forEach(o -> {
			Encounter e = (Encounter) o;
			Date birthdate = e.getPatient().getBirthdate();
			if (birthdate != null) {
				converter.setEffectiveDate(e.getEncounterDatetime());
				agePerEncounterId.put(e.getEncounterId(),
				    converter.convert(new Birthdate(birthdate, e.getPatient().getBirthdateEstimated())));
			}
		});
		return agePerEncounterId;
	}
}
