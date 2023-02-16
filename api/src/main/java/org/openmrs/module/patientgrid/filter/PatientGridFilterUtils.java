package org.openmrs.module.patientgrid.filter;

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
import org.openmrs.module.patientgrid.period.DateRange;
import org.openmrs.module.patientgrid.period.DateRangeConverter;
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

import java.util.*;

import static org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype.*;

/**
 * Contains patient grid filter utility methods
 */
public class PatientGridFilterUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridFilterUtils.class);
	
	/**
	 * Utility method that generates a {@link CohortDefinition} based on the column filters of the
	 * specified {@link PatientGrid}
	 *
	 * @param patientGrid the {@link PatientGrid} object
	 * @return the {@link CohortDefinition} object
	 */
	public static CohortDefinition generateCohortDefinition(PatientGrid patientGrid) {
		final Map<String, CohortDefinition> columnAndCohortDefMap = new HashMap(patientGrid.getColumns().size());
		DateRange periodRange = null;
		for (PatientGridColumn column : patientGrid.getColumns()) {
			if (ENC_DATE.equals(column.getDatatype())) {
				if (!column.getFilters().isEmpty()) {
					for (PatientGridColumnFilter filter : column.getFilters()) {
						periodRange = new DateRangeConverter("UTC", "UTC").convert(filter.getOperand());
					}
				}
				break;
			}
		}
		
		for (PatientGridColumn column : patientGrid.getColumns()) {
			CohortDefinition cohortDef = null;
			if (ENC_AGE.equals(column.getDatatype())) {
				//for age, we will always create a range cohort definition as it's used to filter on the encounter type
				cohortDef = createAgeRangeCohortDefinition(column, periodRange);
			} else if (!column.getFilters().isEmpty()) {
				
				switch (column.getDatatype()) {
					case GENDER:
						cohortDef = createGenderCohortDefinition(column);
						break;
					case OBS:
						cohortDef = createObsCohortDefinition(column, periodRange);
						break;
					case DATAFILTER_LOCATION:
					case DATAFILTER_COUNTRY:
						cohortDef = createLocationCohortDefinition(column, column.getDatatype() == DATAFILTER_COUNTRY);
						break;
					case ENC_DATE:
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
	 * Creates a {@link AgeRangeAtLatestEncounterCohortDefinition} based on the filters for the
	 * specified {@link PatientGridColumn}
	 *
	 * @param column {@link PatientGridColumn} object
	 * @return AgeRangeAtLatestEncounterCohortDefinition
	 */
	private static AgeRangeAtLatestEncounterCohortDefinition createAgeRangeCohortDefinition(PatientGridColumn column,
	        DateRange periodRange) {
		AgeAtEncounterPatientGridColumn ageColumn = (AgeAtEncounterPatientGridColumn) column;
		AgeRangeAtLatestEncounterCohortDefinition def = new AgeRangeAtLatestEncounterCohortDefinition();
		def.setEncounterType(ageColumn.getEncounterType());
		def.setAgeRanges(new ArrayList(column.getFilters().size()));
		def.setPeriodRange(periodRange);
		for (PatientGridColumnFilter filter : column.getFilters()) {
			AgeRange ageRange;
			if (!ageColumn.getConvertToAgeRange()) {
				//TODO support less than 1yr
				Integer age = PatientGridUtils.convert(filter.getOperand(), Integer.class);
				ageRange = new AgeRange(age, age);
			} else {
				ageRange = PatientGridUtils.convert(filter.getOperand(), AgeRange.class);
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
			Location location = PatientGridUtils.convert(filter.getOperand(), Location.class);
			if (location == null) {
				throw new APIException("No location found with uuid: " + filter.getOperand());
			}
			
			def.getLocations().add(location);
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
	private static ObsForLatestEncounterCohortDefinition createObsCohortDefinition(PatientGridColumn column,
	        DateRange periodRange) {
		ObsPatientGridColumn obsColumn = (ObsPatientGridColumn) column;
		ObsForLatestEncounterCohortDefinition obsCohortDef = new ObsForLatestEncounterCohortDefinition();
		Concept concept = obsColumn.getConcept();
		obsCohortDef.setConcept(concept);
		obsCohortDef.setEncounterType(obsColumn.getEncounterType());
		obsCohortDef.setPeriodRange(periodRange);
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
				value = PatientGridUtils.convert(filter.getOperand(), valueType);
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
