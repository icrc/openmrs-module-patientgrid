package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.web.rest.PatientGridReport;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@SubResource(parent = PatientGridResource.class, path = "report", supportedClass = PatientGridReport.class, supportedOpenmrsVersions = {
        SUPPORTED_VERSIONS })
public class PatientGridReportResource extends DelegatingSubResource<PatientGridReport, PatientGrid, PatientGridResource> {
	
	/**
	 * @see DelegatingSubResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("patientGrid", Representation.REF);
		description.addProperty("report");
		return description;
	}
	
	/**
	 * @see DelegatingSubResource#getParent(Object)
	 */
	@Override
	public PatientGrid getParent(PatientGridReport instance) {
		return instance.getPatientGrid();
	}
	
	/**
	 * @see DelegatingSubResource#doGetAll(Object, RequestContext)
	 */
	@Override
	public PageableResult doGetAll(PatientGrid parent, RequestContext context) throws ResponseException {
		ReportData reportData = Context.getService(PatientGridService.class).evaluate(parent);
		SimpleDataSet dataset = (SimpleDataSet) reportData.getDataSets().get("patientData");
		List<Map<String, Object>> report = new ArrayList(dataset.getRows().size());
		dataset.getRows().parallelStream().forEach(row -> {
			report.add(row.getColumnValuesByKey());
		});
		
		return new NeedsPaging(Collections.singletonList(new PatientGridReport(parent, report)), context);
	}
	
	/**
	 * @see DelegatingSubResource#newDelegate()
	 */
	@Override
	public PatientGridReport newDelegate() {
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
	/**
	 * @see DelegatingSubResource#save(Object)
	 */
	@Override
	public PatientGridReport save(PatientGridReport delegate) {
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
	/**
	 * @see DelegatingSubResource#setParent(Object, Object)
	 */
	@Override
	public void setParent(PatientGridReport instance, PatientGrid parent) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see DelegatingSubResource#getByUniqueId(String)
	 */
	@Override
	public PatientGridReport getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see DelegatingSubResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(PatientGridReport delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
	/**
	 * @see DelegatingSubResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(PatientGridReport delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
}
