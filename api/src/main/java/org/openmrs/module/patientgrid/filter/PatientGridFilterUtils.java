package org.openmrs.module.patientgrid.filter;

import static org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype.DATAFILTER_COUNTRY;
import static org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype.ENC_AGE;
import static org.openmrs.module.patientgrid.PatientGridConstants.DATETIME_FORMAT;
import static org.openmrs.module.patientgrid.PatientGridConstants.DATE_FORMAT;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.*;
import org.openmrs.module.patientgrid.filter.definition.AgeRangeAtLatestEncounterCohortDefinition;
import org.openmrs.module.patientgrid.filter.definition.LocationCohortDefinition;
import org.openmrs.module.patientgrid.filter.definition.ObsForLatestEncounterCohortDefinition;
import org.openmrs.module.patientgrid.filter.definition.PeriodCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Contains patient grid filter utility methods
 */
public class PatientGridFilterUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridFilterUtils.class);
	
	protected static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * Utility method that generates a {@link CohortDefinition} based on the column filters of the
	 * specified {@link PatientGrid}
	 *
	 * @param patientGrid the {@link PatientGrid} object
	 * @return the {@link CohortDefinition} object
	 */
	public static CohortDefinition generateCohortDefinition(PatientGrid patientGrid) {
		Map<String, CohortDefinition> columnAndCohortDefMap = new HashMap(patientGrid.getColumns().size());
		for (PatientGridColumn column : patientGrid.getColumns()) {
			CohortDefinition cohortDef = null;
			if (ENC_AGE.equals(column.getDatatype())) {
				//for age, we will always create a range cohort definition as it's used to filter on the encounter type
				cohortDef = createAgeRangeCohortDefinition(column);
			} else if (!column.getFilters().isEmpty()) {
				
				switch (column.getDatatype()) {
					case GENDER:
						cohortDef = createGenderCohortDefinition(column);
						break;
					case OBS:
						cohortDef = createObsCohortDefinition(column);
						break;
					case DATAFILTER_LOCATION:
					case DATAFILTER_COUNTRY:
						cohortDef = createLocationCohortDefinition(column, column.getDatatype() == DATAFILTER_COUNTRY);
						break;
					case ENC_DATE:
						cohortDef = createPeriodCohortDefinition(column);
						break;
					default:
						throw new APIException("Don't know how to filter data for column type: " + column.getDatatype());
				}
			}
			if (cohortDef != null) {
				columnAndCohortDefMap.put(column.getName(), cohortDef);
			}
		}
		
		if (columnAndCohortDefMap.isEmpty()) {
			log.debug("No filters to apply to patient grid {}", patientGrid);
			
			return null;
		}
		
		return createCohortDef(columnAndCohortDefMap, BooleanOperator.AND);
	}
	
	/**
	 * Converts the specified string to the specified type
	 *
	 * @param value the value to convert
	 * @param clazz the type to convert to
	 * @return the converted value
	 */
	protected static <T> T convert(String value, Class<T> clazz) throws APIException {
		Object ret;
		if (Double.class.isAssignableFrom(clazz)) {
			ret = Double.valueOf(value);
		} else if (Integer.class.isAssignableFrom(clazz)) {
			ret = Integer.valueOf(value);
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			ret = Boolean.valueOf(value);
		} else if (Date.class.isAssignableFrom(clazz)) {
			try {
				ret = DATETIME_FORMAT.parse(value);
			}
			catch (ParseException e) {
				try {
					ret = DATE_FORMAT.parse(value);
				}
				catch (ParseException pe) {
					throw new APIException("Failed to convert " + value + " to a date", pe);
				}
			}
		} else if (Concept.class.isAssignableFrom(clazz)) {
			ret = Context.getConceptService().getConceptByUuid(value);
		} else if (Location.class.isAssignableFrom(clazz)) {
			ret = Context.getLocationService().getLocationByUuid(value);
		} else if (AgeRange.class.isAssignableFrom(clazz)) {
			try {
				Map map = MAPPER.readValue(value, Map.class);
				ret = new AgeRange((Integer) map.get("minAge"), (Integer) map.get("maxAge"));
			}
			catch (IOException e) {
				throw new APIException("Failed to convert: " + value + " to an AgeRange", e);
			}
		} else if (PeriodRange.class.isAssignableFrom(clazz)) {
			Map map = null;
			try {
				map = MAPPER.readValue(value, Map.class);
				ret = new PeriodRange(convert((String) map.get("fromDate"), Date.class),
				        convert((String) map.get("toDate"), Date.class));
				
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new APIException("Don't know how to convert operand value to type: " + clazz.getName());
		}
		
		return (T) ret;
	}
	
	/**
	 * Creates a {@link AgeRangeAtLatestEncounterCohortDefinition} based on the filters for the
	 * specified {@link PatientGridColumn}
	 *
	 * @param column {@link PatientGridColumn} object
	 * @return AgeRangeAtLatestEncounterCohortDefinition
	 */
	private static AgeRangeAtLatestEncounterCohortDefinition createAgeRangeCohortDefinition(PatientGridColumn column) {
		AgeAtEncounterPatientGridColumn ageColumn = (AgeAtEncounterPatientGridColumn) column;
		AgeRangeAtLatestEncounterCohortDefinition def = new AgeRangeAtLatestEncounterCohortDefinition();
		def.setEncounterType(ageColumn.getEncounterType());
		def.setAgeRanges(new ArrayList(column.getFilters().size()));
		for (PatientGridColumnFilter filter : column.getFilters()) {
			AgeRange ageRange;
			if (!ageColumn.getConvertToAgeRange()) {
				//TODO support less than 1yr
				Integer age = convert(filter.getOperand(), Integer.class);
				ageRange = new AgeRange(age, age);
			} else {
				ageRange = convert(filter.getOperand(), AgeRange.class);
			}
			
			def.getAgeRanges().add(ageRange);
		}
		
		return def;
	}
	
	/**
	 * Creates a {@link LocationCohortDefinition} based on the filters for the specified
	 * {@link PatientGridColumn}
	 *
	 * @param column {@link PatientGridColumn} object
	 * @param matchOnCountry specifies if the definition is for country or location
	 * @return LocationCohortDefinition
	 */
	private static LocationCohortDefinition createLocationCohortDefinition(PatientGridColumn column,
	        boolean matchOnCountry) {
		LocationCohortDefinition def = new LocationCohortDefinition();
		def.setCountry(matchOnCountry);
		def.setLocations(new ArrayList(column.getFilters().size()));
		for (PatientGridColumnFilter filter : column.getFilters()) {
			Location location = convert(filter.getOperand(), Location.class);
			if (location == null) {
				throw new APIException("No location found with uuid: " + filter.getOperand());
			}
			
			def.getLocations().add(location);
		}
		
		return def;
	}
	
	private static PeriodCohortDefinition createPeriodCohortDefinition(PatientGridColumn column) {
		PeriodCohortDefinition def = new PeriodCohortDefinition();
		EncounterDatePatientGridColumn periodColumn = (EncounterDatePatientGridColumn) column;
		def.setEncounterType(periodColumn.getEncounterType());
		for (PatientGridColumnFilter filter : column.getFilters()) {
			PeriodRange periodRange = convert(filter.getOperand(), PeriodRange.class);
			def.setFromDate(periodRange.getFromDate());
			def.setToDate(periodRange.getToDate());
		}
		return def;
	}
	
	/**
	 * Creates a {@link ObsForLatestEncounterCohortDefinition} based on the filters for the specified
	 * {@link PatientGridColumn}
	 *
	 * @param column {@link PatientGridColumn} object
	 * @return ObsForLatestEncounterCohortDefinition
	 */
	private static ObsForLatestEncounterCohortDefinition createObsCohortDefinition(PatientGridColumn column) {
		ObsPatientGridColumn obsColumn = (ObsPatientGridColumn) column;
		ObsForLatestEncounterCohortDefinition obsCohortDef = new ObsForLatestEncounterCohortDefinition();
		Concept concept = obsColumn.getConcept();
		obsCohortDef.setConcept(concept);
		obsCohortDef.setEncounterType(obsColumn.getEncounterType());
		Class<?> valueType;
		if (concept.getDatatype().isNumeric()) {
			obsCohortDef.setPropertyName("valueNumeric");
			valueType = Double.class;
		} else if (concept.getDatatype().isBoolean()) {
			obsCohortDef.setPropertyName("valueBoolean");
			valueType = Boolean.class;
		} else if (concept.getDatatype().isCoded()) {
			obsCohortDef.setPropertyName("valueCoded");
			valueType = Concept.class;
		} else if (concept.getDatatype().isDate() || concept.getDatatype().isDateTime()) {
			obsCohortDef.setPropertyName("valueDatetime");
			valueType = Date.class;
		} else if (concept.getDatatype().isText()) {
			obsCohortDef.setPropertyName("valueText");
			valueType = String.class;
		} else {
			throw new APIException("Don't know how to filter obs data of datatype: " + concept.getDatatype());
		}
		
		obsCohortDef.setValues(new ArrayList(column.getFilters().size()));
		for (PatientGridColumnFilter filter : column.getFilters()) {
			Object value = filter.getOperand();
			if (!String.class.isAssignableFrom(valueType)) {
				value = convert(filter.getOperand(), valueType);
				if (Concept.class.equals(valueType) && value == null) {
					throw new APIException("No concept found with uuid: " + filter.getOperand());
				}
			}
			
			obsCohortDef.getValues().add(value);
		}
		
		return obsCohortDef;
	}
	
	/**
	 * Creates a {@link GenderCohortDefinition} based on the filters for the specified
	 * {@link PatientGridColumn}
	 *
	 * @param column {@link PatientGridColumn} object
	 * @return CohortDefinition
	 */
	private static GenderCohortDefinition createGenderCohortDefinition(PatientGridColumn column) {
		GenderCohortDefinition cohortDef = new GenderCohortDefinition();
		for (PatientGridColumnFilter filter : column.getFilters()) {
			if ("M".equalsIgnoreCase(filter.getOperand())) {
				cohortDef.setMaleIncluded(true);
			} else if ("F".equalsIgnoreCase(filter.getOperand())) {
				cohortDef.setFemaleIncluded(true);
			} else {
				//TODO Support other values e.g O for other
				throw new APIException("Gender filter only supports M or F values as operands");
			}
		}
		
		return cohortDef;
	}
	
	private static CohortDefinition createCohortDef(Map<String, CohortDefinition> nameAndCohortDefs,
	        BooleanOperator operator) {
		
		//If there is one filter, just return its cohort definition otherwise create a composition cohort
		//definition using OR operator
		if (nameAndCohortDefs.size() == 1) {
			return nameAndCohortDefs.entrySet().iterator().next().getValue();
		}
		
		CompositionCohortDefinition cohortDef = new CompositionCohortDefinition();
		List<String> disjunctions = new ArrayList(nameAndCohortDefs.size());
		for (Map.Entry<String, CohortDefinition> entry : nameAndCohortDefs.entrySet()) {
			cohortDef.addSearch(entry.getKey(), Mapped.noMappings(entry.getValue()));
			disjunctions.add(entry.getKey());
		}
		
		final String compositionString = StringUtils.join(disjunctions, " " + operator + " ");
		if (operator == BooleanOperator.AND) {
			log.debug("CohortDefinition compositionString for all filters -> {}", compositionString);
		}
		
		cohortDef.setCompositionString(compositionString);
		
		return cohortDef;
	}
	
	/**
	 * Evaluates any filters found on the columns on the specified {@link PatientGrid} and returns the
	 * matching patients How it works:
	 * {@link PatientGridFilterUtils#generateCohortDefinition(PatientGrid)} will create a
	 * CohortDefinition used by CohortDefinitionService The custom CohortDefinition are in the package
	 * org.openmrs.module.patientgrid.filter.definition A definition is evalution by an evaluator
	 * defined in the package org.openmrs.module.patientgrid.filter.evaluator For instance
	 * {@link AgeRangeAtLatestEncounterCohortDefinition} is evaluated
	 * {@link org.openmrs.module.patientgrid.filter.evaluator.AgeRangeAtLatestEncounterCohortDefinitionEvaluator}
	 * thanks to the annotation Handler
	 *
	 * @param patientGrid the {@link PatientGrid} object
	 * @param context the {@link EvaluationContext} object
	 * @return a Cohort of matching patients or null if no filters are found
	 * @throws EvaluationException
	 */
	public static Cohort filterPatients(PatientGrid patientGrid, EvaluationContext context) throws EvaluationException {
		if (context == null) {
			context = new EvaluationContextPersistantCache();
		}
		CohortDefinition cohortDef = PatientGridFilterUtils.generateCohortDefinition(patientGrid);
		if (cohortDef == null) {
			return null;
		}
		
		log.debug("Filtering patients for patient grid {}", patientGrid);
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDef, context);
		
		stopWatch.stop();
		
		log.debug("Running filters for patient grid {} completed in {}", patientGrid, stopWatch.toString());
		
		return cohort;
	}
	
}
