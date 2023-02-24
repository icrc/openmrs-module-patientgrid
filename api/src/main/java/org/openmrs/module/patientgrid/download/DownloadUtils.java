package org.openmrs.module.patientgrid.download;

import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.*;
import org.openmrs.module.patientgrid.filter.ObjectWithDateRange;
import org.openmrs.module.patientgrid.filter.PatientGridFilterUtils;
import org.openmrs.module.patientgrid.period.DateRange;
import org.openmrs.module.patientgrid.period.DateRangeConverter;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype.ENC_DATE;

/**
 * Contains utilities to generate the downloadable patient grid report data
 */
public class DownloadUtils {
	
	private static final Logger log = LoggerFactory.getLogger(DownloadUtils.class);
	
	private static DataSetDefinitionService dataSetService;
	
	public static ExtendedDataSet evaluate(PatientGrid patientGrid) {
		log.debug("Generating downloadable patient grid report data for patient grid: {}", patientGrid);
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		try {
			PatientDataSetDefinition dataSetDef = PatientGridUtils.createPatientDataSetDefinition(patientGrid, false);
			String clientTimezone = PatientGridUtils.getCurrentUserTimeZone();
			DateRange periodRange = null;
			for (PatientGridColumn column : patientGrid.getColumns()) {
				if (ENC_DATE.equals(column.getDatatype())) {
					if (!column.getFilters().isEmpty()) {
						for (PatientGridColumnFilter filter : column.getFilters()) {
							periodRange = new DateRangeConverter(clientTimezone).convert(filter.getOperand());
						}
					}
					break;
				}
			}
			
			final DateRange pr = periodRange;
			PatientGridUtils.getEncounterTypes(patientGrid).forEach(type -> {
				AllEncountersPatientDataDefinition encDef = new AllEncountersPatientDataDefinition();
				encDef.setEncounterType(type);
				encDef.setPatientGrid(patientGrid);
				encDef.setPeriodRange(pr);
				dataSetDef.addColumn(type.getUuid(), encDef, (String) null);
			});
			
			EvaluationContext context = new EvaluationContextPersistantCache();
			ObjectWithDateRange<Cohort> cohortAndDate = PatientGridFilterUtils.filterPatients(patientGrid, context,
			    clientTimezone);
			Cohort cohort = cohortAndDate == null ? null : cohortAndDate.getObject();
			if (cohort == null) {
				cohort = patientGrid.getCohort();
			}
			
			if (cohort != null && cohort.isEmpty()) {
				log.info("Cohort is empty, nothing to evaluate");
				return new ExtendedDataSet(new SimpleDataSet(dataSetDef, context),
				        cohortAndDate == null ? null : cohortAndDate.getDateRange());
			}
			
			context.setBaseCohort(cohort);
			if (dataSetService == null) {
				dataSetService = Context.getService(DataSetDefinitionService.class);
			}
			
			SimpleDataSet dataSet = (SimpleDataSet) dataSetService.evaluate(dataSetDef, context);
			
			stopWatch.stop();
			
			log.debug("Generating downloadable patient grid report data for patient grid {}  completed in {}", patientGrid,
			    stopWatch.toString());
			ExtendedDataSet res = new ExtendedDataSet(dataSet, cohortAndDate.getDateRange());
			//TODO: go on configuration here
			return res;
		}
		catch (EvaluationException e) {
			throw new APIException(
			        "Failed to generate downloadable patient grid report data for patient grid: " + patientGrid, e);
		}
	}
	
}
