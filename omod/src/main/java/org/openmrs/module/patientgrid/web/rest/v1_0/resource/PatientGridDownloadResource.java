package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.download.DownloadUtils;
import org.openmrs.module.patientgrid.web.rest.PatientGridDownload;
import org.openmrs.module.patientgrid.web.rest.PatientGridReport;
import org.openmrs.module.patientgrid.web.rest.ReportMetadata;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@SubResource(parent = PatientGridResource.class, path = "download", supportedClass = PatientGridDownload.class, supportedOpenmrsVersions = {
        SUPPORTED_VERSIONS })
public class PatientGridDownloadResource extends BasePatientGridDataResource<PatientGridDownload> {
	
	/**
	 * @see BasePatientGridDataResource#evaluate(PatientGrid, RequestContext)
	 */
	@Override
	public ExtendedDataSet evaluate(PatientGrid parent, RequestContext context) throws ResponseException {
		return DownloadUtils.evaluate(parent);
	}
	
	@Override
	protected PatientGridDownload create(ReportMetadata reportMetadata, PatientGrid patientGrid, List report) {
		return new PatientGridDownload(reportMetadata, patientGrid, report);
	}
	
}
