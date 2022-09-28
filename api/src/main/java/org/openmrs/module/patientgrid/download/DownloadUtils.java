package org.openmrs.module.patientgrid.download;

import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.filter.PatientGridFilterUtils;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains utilities to generate the downloadable patient grid report data
 */
public class DownloadUtils {
	
	private static final Logger log = LoggerFactory.getLogger(DownloadUtils.class);
	
	private static DataSetDefinitionService dataSetService;
	
	public static SimpleDataSet evaluate(PatientGrid patientGrid) {
		if (log.isDebugEnabled()) {
			log.debug("Generating downloadable patient grid report data for patient grid: " + patientGrid);
		}
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		try {
			PatientDataSetDefinition dataSetDef = PatientGridUtils.createPatientDataSetDefinition(patientGrid, false);
			PatientGridUtils.getEncounterTypes(patientGrid).forEach(type -> {
				AllEncountersPatientDataDefinition encDef = new AllEncountersPatientDataDefinition();
				encDef.setEncounterType(type);
				encDef.setPatientGrid(patientGrid);
				dataSetDef.addColumn(type.getUuid(), encDef, (String) null);
			});
			
			EvaluationContext context = new EvaluationContext();
			Cohort cohort = PatientGridFilterUtils.filterPatients(patientGrid, context);
			if (cohort == null) {
				cohort = patientGrid.getCohort();
			}
			
			if (cohort != null && cohort.isEmpty()) {
				log.info("Cohort is empty, nothing to evaluate");
				return new SimpleDataSet(dataSetDef, context);
			}
			
			context.setBaseCohort(cohort);
			if (dataSetService == null) {
				dataSetService = Context.getService(DataSetDefinitionService.class);
			}
			
			SimpleDataSet dataSet = (SimpleDataSet) dataSetService.evaluate(dataSetDef, context);
			
			stopWatch.stop();
			
			if (log.isDebugEnabled()) {
				log.debug("Generating downloadable patient grid report data for patient grid " + patientGrid
				        + " completed in " + stopWatch.toString());
			}
			
			return dataSet;
		}
		catch (EvaluationException e) {
			throw new APIException(
			        "Failed to generate downloadable patient grid report data for patient grid: " + patientGrid, e);
		}
	}
	
}
