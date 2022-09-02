package org.openmrs.module.patientgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains patient grid filter utility methods
 */
public class PatientGridFilterUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridFilterUtils.class);
	
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
					if (!column.getFilters().isEmpty()) {
						Map<String, CohortDefinition> cohortDefs = new HashMap(column.getFilters().size());
						for (PatientGridColumnFilter filter : column.getFilters()) {
							GenderCohortDefinition cohortDef = new GenderCohortDefinition();
							if ("M".equals(filter.getOperand())) {
								cohortDef.setMaleIncluded(true);
							} else if ("F".equals(filter.getOperand())) {
								cohortDef.setFemaleIncluded(true);
							} else {
								//TODO Support other values e.g O for other
								throw new APIException("Gender filter only supports M or F values as operands");
							}
							
							cohortDefs.put(filter.getName(), cohortDef);
						}
						
						columnCohortDefMap.put(column.getName(), createCohortDef(cohortDefs, BooleanOperator.OR));
					}
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
	
}
