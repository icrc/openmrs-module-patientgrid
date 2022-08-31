package org.openmrs.module.patientgrid.api.impl;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.api.db.PatientGridDAO;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class PatientGridServiceImpl extends BaseOpenmrsService implements PatientGridService {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridServiceImpl.class);
	
	private PatientGridDAO dao;
	
	/**
	 * Sets the dao
	 *
	 * @param dao the dao to set
	 */
	public void setDao(PatientGridDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see PatientGridService#getPatientGrid(Integer)
	 */
	@Override
	public PatientGrid getPatientGrid(Integer patientGridId) {
		return dao.getPatientGrid(patientGridId);
	}
	
	/**
	 * @see PatientGridService#getPatientGridByUuid(String)
	 */
	@Override
	public PatientGrid getPatientGridByUuid(String uuid) {
		return dao.getPatientGridByUuid(uuid);
	}
	
	/**
	 * @see PatientGridService#getPatientGrids(boolean)
	 */
	@Override
	public List<PatientGrid> getPatientGrids(boolean includeRetired) {
		return dao.getPatientGrids(includeRetired);
	}
	
	/**
	 * @see PatientGridService#savePatientGrid(PatientGrid)
	 */
	@Transactional
	@Override
	public PatientGrid savePatientGrid(PatientGrid patientGrid) {
		return dao.savePatientGrid(patientGrid);
	}
	
	/**
	 * @see PatientGridService#retirePatientGrid(PatientGrid, String)
	 */
	@Transactional
	@Override
	public PatientGrid retirePatientGrid(PatientGrid patientGrid, String retireReason) {
		return Context.getService(PatientGridService.class).savePatientGrid(patientGrid);
	}
	
	/**
	 * @see PatientGridService#unretirePatientGrid(PatientGrid)
	 */
	@Transactional
	@Override
	public PatientGrid unretirePatientGrid(PatientGrid patientGrid) {
		return Context.getService(PatientGridService.class).savePatientGrid(patientGrid);
	}
	
	/**
	 * @see PatientGridService#evaluate(PatientGrid)
	 */
	public ReportData evaluate(PatientGrid patientGrid) {
		log.info("Generating report for patient grid: " + patientGrid);
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		ReportDefinition reportDef = PatientGridUtils.convertToReportDefinition(patientGrid);
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(patientGrid.getCohort());
		
		try {
			ReportData reportData = rds.evaluate(reportDef, context);
			
			log.info("Report for patient grid " + patientGrid + " completed in " + stopWatch.toString());
			
			return reportData;
		}
		catch (EvaluationException e) {
			throw new APIException("Failed to evaluate patient grid: " + patientGrid, e);
		}
		finally {
			stopWatch.stop();
		}
	}
	
}
