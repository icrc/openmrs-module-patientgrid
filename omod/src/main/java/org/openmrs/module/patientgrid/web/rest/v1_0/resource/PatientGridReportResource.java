package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.web.rest.PatientGridReport;
import org.openmrs.module.patientgrid.web.rest.ReportMetadata;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.PARAM_REFRESH;
import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

@SubResource(parent = PatientGridResource.class, path = "report", supportedClass = PatientGridReport.class, supportedOpenmrsVersions = {
        SUPPORTED_VERSIONS })
public class PatientGridReportResource extends BasePatientGridDataResource<PatientGridReport> {
	
	/**
	 * @see BasePatientGridDataResource#evaluate(PatientGrid, RequestContext)
	 */
	@Override
	public ExtendedDataSet evaluate(PatientGrid parent, RequestContext context) throws ResponseException {
		if (Boolean.valueOf(context.getParameter(PARAM_REFRESH))) {
			return Context.getService(PatientGridService.class).evaluateIgnoreCache(parent);
		} else {
			return Context.getService(PatientGridService.class).evaluate(parent);
		}
	}
	
	@Override
	protected PatientGridReport create(ReportMetadata reportMetadata, PatientGrid patientGrid, List report) {
		return new PatientGridReport(reportMetadata, patientGrid, report);
	}
}
