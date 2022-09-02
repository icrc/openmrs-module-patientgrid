package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridConstants.GP_AGE_RANGES;
import static org.openmrs.module.reporting.common.Age.Unit.YEARS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.AgeRangeConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientGridUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridUtils.class);
	
	private static final PatientGridObsConverter OBS_CONVERTER = new PatientGridObsConverter();
	
	private static final PropertyConverter COUNTRY_CONVERTER = new PropertyConverter(String.class, "country");
	
	private static final PatientLocationDataDefinition LOCATION_DATA_DEF = new PatientLocationDataDefinition();
	
	private static final PreferredNameDataDefinition NAME_DATA_DEF = new PreferredNameDataDefinition();
	
	private static final GenderDataDefinition GENDER_DATA_DEF = new GenderDataDefinition();
	
	private static final PersonUuidDataDefinition UUID_DATA_DEF = new PersonUuidDataDefinition();
	
	private static final ObjectFormatter OBJECT_CONVERTER = new ObjectFormatter();
	
	public static ReportDefinition convertToReportDefinition(PatientGrid patientGrid) {
		ReportDefinition reportDef = new ReportDefinition();
		reportDef.setName(patientGrid.getName());
		reportDef.setDescription(patientGrid.getDescription());
		PatientDataSetDefinition patientData = new PatientDataSetDefinition();
		patientData.addColumn(PatientGridConstants.COLUMN_UUID, UUID_DATA_DEF, (String) null);
		
		for (PatientGridColumn columnDef : patientGrid.getColumns()) {
			switch (columnDef.getDatatype()) {
				case NAME:
					patientData.addColumn(columnDef.getName(), NAME_DATA_DEF, (String) null, OBJECT_CONVERTER);
					break;
				case GENDER:
					patientData.addColumn(columnDef.getName(), GENDER_DATA_DEF, (String) null);
					break;
				case ENC_AGE:
					AgeAtEncounterPatientGridColumn ageColumn = (AgeAtEncounterPatientGridColumn) columnDef;
					PatientAgeAtEncounterDataDefinition def = new PatientAgeAtEncounterDataDefinition();
					def.setEncounterType(ageColumn.getEncounterType());
					if (ageColumn.getConvertToAgeRange()) {
						AgeRangeConverter converter = new AgeRangeConverter();
						getAgeRanges().forEach(r -> converter.addAgeRange(r));
						AgeAtEncounterPatientGridColumn ageRangeColumn = (AgeAtEncounterPatientGridColumn) columnDef;
						PatientAgeAtEncounterDataDefinition ageRangeDef = new PatientAgeAtEncounterDataDefinition();
						ageRangeDef.setEncounterType(ageRangeColumn.getEncounterType());
						patientData.addColumn(columnDef.getName(), def, (String) null, converter);
					} else {
						patientData.addColumn(columnDef.getName(), def, (String) null);
					}
					
					break;
				case OBS:
					ObsPatientGridColumn obsColumn = (ObsPatientGridColumn) columnDef;
					ObsForMostRecentEncounterDataDefinition obsDataDef = new ObsForMostRecentEncounterDataDefinition();
					obsDataDef.setConcept(obsColumn.getConcept());
					obsDataDef.setEncounterType(obsColumn.getEncounterType());
					patientData.addColumn(columnDef.getName(), obsDataDef, (String) null, OBS_CONVERTER);
					break;
				case DATAFILTER_LOCATION:
					patientData.addColumn(columnDef.getName(), LOCATION_DATA_DEF, (String) null);
					break;
				case DATAFILTER_COUNTRY:
					patientData.addColumn(columnDef.getName(), LOCATION_DATA_DEF, (String) null, COUNTRY_CONVERTER);
					break;
				default:
					throw new APIException("Don't know how to handle column type: " + columnDef.getDatatype());
			}
		}
		
		reportDef.addDataSetDefinition("patientData", patientData, null);
		
		return reportDef;
	}
	
	/**
	 * Fetches the most recent encounter data for the specified cohort of patients matching the given
	 * encounter type.
	 *
	 * @param type the encounter type to match
	 * @param cohort the base cohort whose encounters to return
	 * @param mostRecentOnly specifies whether to return only the most recent encounter for each patient
	 *            or their encounter history
	 * @return a map of patient ids to encounters
	 */
	public static Map<Integer, Object> getMostRecentEncounters(EncounterType type, Cohort cohort, boolean mostRecentOnly)
	    throws EvaluationException {
		
		if (cohort.size() > 1) {
			log.info("Fetching most recent encounters for encounter type: " + type);
		}
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		EncountersForPatientDataDefinition encDef = new EncountersForPatientDataDefinition();
		encDef.setTypes(Collections.singletonList(type));
		if (mostRecentOnly) {
			encDef.setWhich(TimeQualifier.LAST);
		}
		
		EvaluationContext encContext = new EvaluationContext();
		encContext.setBaseCohort(cohort);
		Map<Integer, Object> results = Context.getService(PatientDataService.class).evaluate(encDef, encContext).getData();
		
		stopWatch.stop();
		
		if (cohort.size() > 1) {
			log.info(
			    "Fetching most recent encounters for encounter type: " + type + " completed in " + stopWatch.toString());
		}
		
		return results;
	}
	
	/**
	 * Gets the observation from the specified encounter with a question concept that matches the
	 * specified concept ignoring obs groupings and voided obs.
	 *
	 * @param encounter the encounter containing the obs to search
	 * @param concept the question concept to match
	 * @return Observation if match is found otherwise null
	 */
	public static Obs getObsByConcept(Encounter encounter, Concept concept) {
		List<Obs> matches = encounter.getObsAtTopLevel(false).stream()
		        .filter(o -> o.getConcept().equals(concept) && !o.hasGroupMembers(true)).collect(Collectors.toList());
		
		if (matches.size() > 1) {
			throw new APIException("Found multiple obs with question concept " + concept + " for encounter " + encounter);
		}
		
		if (matches.size() == 1) {
			return matches.get(0);
		}
		
		return null;
	}
	
	/**
	 * Parses the specified age range string to generate a list of {@link AgeRange} objects
	 *
	 * @param ageRangesAsString the age range string to parse
	 * @return a list of {@link AgeRange} objects
	 */
	public static List<AgeRange> parseAgeRangeString(String ageRangesAsString) {
		String[] ranges = ageRangesAsString.split(",");
		List<AgeRange> ageRanges = new ArrayList(ranges.length);
		for (int i = 0; i < ranges.length; i++) {
			if (i < ranges.length - 1) {
				String[] rangeAndLabel = ranges[i].trim().split(":");
				String range;
				String label;
				if (rangeAndLabel.length == 1) {
					range = rangeAndLabel[0].trim();
					label = range;
				} else {
					range = rangeAndLabel[0].trim();
					label = rangeAndLabel[1].trim();
				}
				
				String[] minAndMax = range.split("-");
				Integer min = Integer.parseInt(minAndMax[0]);
				Integer max = Integer.parseInt(minAndMax[1]);
				ageRanges.add(new AgeRange(min, YEARS, max, YEARS, label));
			} else {
				ageRanges.add(new AgeRange(ageRanges.get(i - 1).getMaxAge() + 1, YEARS, null, null, ranges[i].trim()));
			}
		}
		
		return ageRanges;
	}
	
	/**
	 * Utility method that evaluates the column filters on the specified {@link PatientGrid} and returns
	 * the evaluated cohort
	 * 
	 * @param patientGrid the grid with the column filters to evaluate
	 * @return the evaluate cohort
	 * @throws EvaluationException
	 */
	protected static Cohort filterPatients(PatientGrid patientGrid) throws EvaluationException {
		Map<String, CohortDefinition> columnCohortDefMap = new HashMap(patientGrid.getColumns().size());
		for (PatientGridColumn column : patientGrid.getColumns()) {
			switch (column.getDatatype()) {
				case NAME:
					//TODO
					break;
				case GENDER:
					
					break;
				case ENC_AGE:
					//TODO
					break;
				case OBS:
					//TODO
					break;
				case DATAFILTER_LOCATION:
					//TODO
					break;
				case DATAFILTER_COUNTRY:
					//TODO
					break;
				default:
					throw new APIException("Don't know how to filter data for column type: " + column.getDatatype());
			}
		}
		
		if (columnCohortDefMap.isEmpty()) {
			return null;
		}
		
		CohortDefinition rootCohortDef = createCohortDef(columnCohortDefMap, BooleanOperator.AND);
		
		return Context.getService(CohortDefinitionService.class).evaluate(rootCohortDef, null);
	}
	
	private static CohortDefinition createCohortDef(Map<String, CohortDefinition> cohortDefs, BooleanOperator operator) {
		//If there is one filter, just return its cohort definition otherwise create a composition cohort 
		//definition using OR operator
		if (cohortDefs.size() == 1) {
			return cohortDefs.entrySet().iterator().next().getValue();
		}
		
		CompositionCohortDefinition columnCohortDef = new CompositionCohortDefinition();
		List<String> disjunctions = new ArrayList(cohortDefs.size());
		for (Map.Entry<String, CohortDefinition> entry : cohortDefs.entrySet()) {
			columnCohortDef.addSearch(entry.getKey(), Mapped.noMappings(entry.getValue()));
			disjunctions.add(entry.getKey());
		}
		
		columnCohortDef.setCompositionString(StringUtils.join(disjunctions, operator.name()));
		
		return columnCohortDef;
	}
	
	private static List<AgeRange> getAgeRanges() {
		String ageRange = Context.getAdministrationService().getGlobalProperty(GP_AGE_RANGES);
		if (StringUtils.isBlank(ageRange)) {
			throw new APIException(
			        "No ranges defined, please set the value for the global property named: " + GP_AGE_RANGES);
		}
		
		return parseAgeRangeString(ageRange);
	}
	
}
