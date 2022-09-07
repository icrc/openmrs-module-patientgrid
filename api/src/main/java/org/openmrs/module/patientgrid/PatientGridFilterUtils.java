package org.openmrs.module.patientgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.common.RangeComparator;
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
						GenderCohortDefinition genderCohortDef = new GenderCohortDefinition();
						for (PatientGridColumnFilter filter : column.getFilters()) {
							if ("M".equalsIgnoreCase(filter.getOperand().toString())) {
								genderCohortDef.setMaleIncluded(true);
							} else if ("F".equalsIgnoreCase(filter.getOperand().toString())) {
								genderCohortDef.setFemaleIncluded(true);
							} else {
								//TODO Support other values e.g O for other
								throw new APIException("Gender filter only supports M or F values as operands");
							}
						}
						
						cohortDef = genderCohortDef;
						break;
					case ENC_AGE:
						cohortDef = null;
						//TODO
						break;
					case OBS:
						ObsPatientGridColumn obsColumn = (ObsPatientGridColumn) column;
						BaseObsCohortDefinition obsCohortDef;
						if (obsColumn.getConcept().getDatatype().isNumeric()) {
							NumericObsCohortDefinition numericCohortDef = new NumericObsCohortDefinition();
							for (PatientGridColumnFilter filter : column.getFilters()) {
								numericCohortDef.setOperator1(RangeComparator.EQUAL);
								numericCohortDef.setValue1((Double) filter.getOperand());
							}
							
							obsCohortDef = numericCohortDef;
						} else {
							throw new APIException("Don't know how to filter obs data of datatype: "
							        + obsColumn.getConcept().getDatatype());
						}
						
						obsCohortDef.setQuestion(obsColumn.getConcept());
						obsCohortDef.addEncounterType(obsColumn.getEncounterType());
						obsCohortDef.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
						
						cohortDef = obsCohortDef;
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
		if (log.isDebugEnabled()) {
			log.debug("CohortDefinition compositionString after all filters -> " + compositionString);
		}
		
		cohortDef.setCompositionString(compositionString);
		
		return cohortDef;
	}
	
}
