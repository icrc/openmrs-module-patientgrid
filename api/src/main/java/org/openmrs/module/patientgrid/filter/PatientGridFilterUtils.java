package org.openmrs.module.patientgrid.filter;

import static org.openmrs.module.patientgrid.PatientGridConstants.DATETIME_FORMAT;
import static org.openmrs.module.patientgrid.PatientGridConstants.DATE_FORMAT;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.ObsPatientGridColumn;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	protected static CohortDefinition generateCohortDefinition(PatientGrid patientGrid) {
		Map<String, CohortDefinition> columnAndCohortDefMap = new HashMap(patientGrid.getColumns().size());
		for (PatientGridColumn column : patientGrid.getColumns()) {
			if (!column.getFilters().isEmpty()) {
				CohortDefinition cohortDef;
				switch (column.getDatatype()) {
					case GENDER:
						cohortDef = createGenderCohortDefinition(column);
						break;
					case ENC_AGE:
						cohortDef = null;
						//TODO
						break;
					case OBS:
						cohortDef = createObsCohortDefinition(column);
						break;
					case DATAFILTER_LOCATION:
						cohortDef = null;
						//TODO
						break;
					case DATAFILTER_COUNTRY:
						cohortDef = null;
						//TODO
						break;
					default:
						throw new APIException("Don't know how to filter data for column type: " + column.getDatatype());
				}
				
				columnAndCohortDefMap.put(column.getName(), cohortDef);
			}
		}
		
		if (columnAndCohortDefMap.isEmpty()) {
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
	protected static Object convert(String value, Class<?> clazz) {
		if (Double.class.isAssignableFrom(clazz)) {
			return Double.valueOf(value);
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return Boolean.valueOf(value);
		} else if (Date.class.isAssignableFrom(clazz)) {
			try {
				return DATETIME_FORMAT.parse(value);
			}
			catch (ParseException e) {
				try {
					return DATE_FORMAT.parse(value);
				}
				catch (ParseException parseException) {
					throw new APIException("Failed to convert " + value + " to a date", parseException);
				}
			}
		} else if (Concept.class.isAssignableFrom(clazz)) {
			return Context.getConceptService().getConceptByUuid(value);
		}
		
		throw new APIException("Don't know how to convert operand value to type: " + clazz.getName());
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
			if ("M".equalsIgnoreCase(filter.getOperand().toString())) {
				cohortDef.setMaleIncluded(true);
			} else if ("F".equalsIgnoreCase(filter.getOperand().toString())) {
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
			if (log.isDebugEnabled()) {
				log.debug("CohortDefinition compositionString for all filters -> " + compositionString);
			}
		}
		
		cohortDef.setCompositionString(compositionString);
		
		return cohortDef;
	}
	
}
