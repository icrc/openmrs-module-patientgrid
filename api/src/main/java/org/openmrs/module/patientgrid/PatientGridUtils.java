package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridConstants.GP_AGE_RANGES;
import static org.openmrs.module.patientgrid.PatientGridConstants.OBS_CONVERTER;
import static org.openmrs.module.reporting.common.Age.Unit.YEARS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.converter.PatientGridAgeConverter;
import org.openmrs.module.patientgrid.definition.AgeAtLatestEncounterPatientDataDefinition;
import org.openmrs.module.patientgrid.definition.LocationPatientDataDefinition;
import org.openmrs.module.patientgrid.definition.ObsForLatestEncounterPatientDataDefinition;
import org.openmrs.module.patientgrid.definition.PersonUuidDataDefinition;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.AgeRangeConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientGridUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridUtils.class);
	
	private static final DataConverter COUNTRY_CONVERTER = new PropertyConverter(String.class, "country");
	
	private static final LocationPatientDataDefinition LOCATION_DATA_DEF = new LocationPatientDataDefinition();
	
	private static final PreferredNameDataDefinition NAME_DATA_DEF = new PreferredNameDataDefinition();
	
	private static final GenderDataDefinition GENDER_DATA_DEF = new GenderDataDefinition();
	
	private static final PersonUuidDataDefinition UUID_DATA_DEF = new PersonUuidDataDefinition();
	
	private static final DataConverter OBJECT_CONVERTER = new ObjectFormatter();
	
	private static final DataConverter AGE_CONVERTER = new PatientGridAgeConverter();
	
	/**
	 * Create a {@link PatientDataSetDefinition} instance from the specified {@link PatientGrid} object
	 * 
	 * @param patientGrid {@link PatientGrid} object
	 * @param includeObs specifies if obs data should include or not
	 * @return PatientDataSetDefinition
	 */
	public static PatientDataSetDefinition createPatientDataSetDefinition(PatientGrid patientGrid, boolean includeObs) {
		PatientDataSetDefinition dataSetDef = new PatientDataSetDefinition();
		dataSetDef.addColumn(PatientGridConstants.COLUMN_UUID, UUID_DATA_DEF, (String) null);
		
		for (PatientGridColumn columnDef : patientGrid.getColumns()) {
			if (!includeObs && columnDef.getDatatype() == ColumnDatatype.OBS) {
				continue;
			}
			
			switch (columnDef.getDatatype()) {
				case NAME:
					dataSetDef.addColumn(columnDef.getName(), NAME_DATA_DEF, (String) null, OBJECT_CONVERTER);
					break;
				case GENDER:
					dataSetDef.addColumn(columnDef.getName(), GENDER_DATA_DEF, (String) null);
					break;
				case ENC_AGE:
					AgeAtEncounterPatientGridColumn ageColumn = (AgeAtEncounterPatientGridColumn) columnDef;
					AgeAtLatestEncounterPatientDataDefinition def = new AgeAtLatestEncounterPatientDataDefinition();
					def.setEncounterType(ageColumn.getEncounterType());
					if (ageColumn.getConvertToAgeRange()) {
						//TODO Define at class level so we construct once
						AgeRangeConverter converter = new AgeRangeConverter();
						getAgeRanges().forEach(r -> converter.addAgeRange(r));
						dataSetDef.addColumn(columnDef.getName(), def, (String) null, converter);
					} else {
						dataSetDef.addColumn(columnDef.getName(), def, (String) null, AGE_CONVERTER);
					}
					
					break;
				case OBS:
					ObsPatientGridColumn obsColumn = (ObsPatientGridColumn) columnDef;
					ObsForLatestEncounterPatientDataDefinition obsDataDef = new ObsForLatestEncounterPatientDataDefinition();
					obsDataDef.setConcept(obsColumn.getConcept());
					obsDataDef.setEncounterType(obsColumn.getEncounterType());
					dataSetDef.addColumn(columnDef.getName(), obsDataDef, (String) null, OBS_CONVERTER);
					break;
				case DATAFILTER_LOCATION:
					dataSetDef.addColumn(columnDef.getName(), LOCATION_DATA_DEF, (String) null, OBJECT_CONVERTER);
					break;
				case DATAFILTER_COUNTRY:
					dataSetDef.addColumn(columnDef.getName(), LOCATION_DATA_DEF, (String) null, COUNTRY_CONVERTER);
					break;
				default:
					throw new APIException("Don't know how to handle column type: " + columnDef.getDatatype());
			}
		}
		
		return dataSetDef;
	}
	
	/**
	 * Fetches the encounters for the specified cohort of patients matching the given encounter type.
	 *
	 * @param type the encounter type to match
	 * @param context {@link EvaluationContext} object
	 * @param mostRecentOnly specifies whether to return only the most recent encounter for each patient
	 *            or their encounter history
	 * @return a map of patient ids to encounters
	 */
	public static Map<Integer, Object> getEncounters(EncounterType type, EvaluationContext context, boolean mostRecentOnly)
	        throws EvaluationException {
		
		Cohort cohort = null;
		if (context != null) {
			cohort = context.getBaseCohort();
		}
		
		if (cohort == null || cohort.size() > 1) {
			if (mostRecentOnly) {
				log.info("Fetching most recent encounters of type: " + type);
			} else {
				log.info("Fetching encounters of type: " + type);
			}
		}
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		EncountersForPatientDataDefinition encDef = new EncountersForPatientDataDefinition();
		encDef.setTypes(Collections.singletonList(type));
		if (mostRecentOnly) {
			encDef.setWhich(TimeQualifier.LAST);
		}
		
		Map<Integer, Object> results = Context.getService(PatientDataService.class).evaluate(encDef, context).getData();
		
		stopWatch.stop();
		
		if (cohort == null || cohort.size() > 1) {
			if (mostRecentOnly) {
				log.info("Fetching most recent encounters of type: " + type + " completed in " + stopWatch.toString());
			} else {
				log.info("Fetching encounters of type: " + type + " completed in " + stopWatch.toString());
			}
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
	protected static List<AgeRange> parseAgeRangeString(String ageRangesAsString) {
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
	 * Gets the list of age ranges
	 * 
	 * @return list of AgeRange objects
	 */
	public static List<AgeRange> getAgeRanges() {
		//TODO cache the age ranges and update with a GlobalPropertyListener
		String ageRange = Context.getAdministrationService().getGlobalProperty(GP_AGE_RANGES);
		if (StringUtils.isBlank(ageRange)) {
			throw new APIException(
			        "No age ranges defined, please set the value for the global property named: " + GP_AGE_RANGES);
		}
		
		return parseAgeRangeString(ageRange);
	}
	
	/**
	 * Gets all encounter types defined on all ObsPatientGridColumns in the specified
	 * {@link PatientGrid}
	 * 
	 * @param patientGrid {@link PatientGrid} object
	 * @return set of {@link EncounterType} objects
	 */
	public static Set<EncounterType> getEncounterTypes(PatientGrid patientGrid) {
		return patientGrid.getObsColumns().stream().map(c -> c.getEncounterType()).collect(Collectors.toSet());
	}
	
}
