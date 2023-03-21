package org.openmrs.module.patientgrid.download;

import org.openmrs.api.APIException;
import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.api.impl.PatientGridServiceImpl;
import org.openmrs.module.patientgrid.filter.PatientGridFilterUtils;
import org.openmrs.module.patientgrid.filter.definition.LocationCohortDefinition;
import org.openmrs.module.patientgrid.period.DateRange;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains utilities to generate the downloadable patient grid report data
 */
public class DownloadUtils {
	
	/**
	 * Utility class
	 */
	private DownloadUtils() {
		
	}
	
	private static final Logger log = LoggerFactory.getLogger(DownloadUtils.class);
	
	public static ExtendedDataSet evaluate(PatientGrid patientGrid) {
		log.debug("Generating downloadable patient grid report data for patient grid: {}", patientGrid);
		
		try {
			String clientTimezone = PatientGridUtils.getCurrentUserTimeZone();
			PatientDataSetDefinition dataSetDef = PatientGridUtils.createPatientDataSetDefinition(patientGrid, false,
			    clientTimezone);
			DateRange periodRange = PatientGridFilterUtils.extractPeriodRange(patientGrid, clientTimezone);
			LocationCohortDefinition locationCohortDefinition = PatientGridFilterUtils.extractLocations(patientGrid);
			
			final DateRange pr = periodRange;
			PatientGridUtils.getEncounterTypes(patientGrid).forEach(type -> {
				AllEncountersPatientDataDefinition encDef = new AllEncountersPatientDataDefinition();
				encDef.setEncounterType(type);
				encDef.setPatientGrid(patientGrid);
				encDef.setPeriodRange(pr);
				encDef.setLocationCohortDefinition(locationCohortDefinition);
				dataSetDef.addColumn(type.getUuid(), encDef, (String) null);
			});
			return PatientGridServiceImpl.createExtendedDataSet(patientGrid, clientTimezone, dataSetDef);
		}
		catch (EvaluationException e) {
			throw new APIException(
			        "Failed to generate downloadable patient grid report data for patient grid: " + patientGrid, e);
		}
	}
	
}
