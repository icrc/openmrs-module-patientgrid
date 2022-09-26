package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.web.rest.PatientGridReport;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@SubResource(parent = PatientGridResource.class, path = "report", supportedClass = PatientGridReport.class, supportedOpenmrsVersions = {
        SUPPORTED_VERSIONS })
public class PatientGridReportResource extends BasePatientGridDataResource<PatientGridReport> {
	
	/**
	 * @see BasePatientGridDataResource#evaluate(PatientGrid, RequestContext)
	 */
	@Override
	public SimpleDataSet evaluate(PatientGrid parent, RequestContext context) throws ResponseException {
		return Context.getService(PatientGridService.class).evaluate(parent);
	}
	
}
