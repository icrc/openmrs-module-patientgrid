package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.web.rest.BasePatientGridData;
import org.openmrs.module.patientgrid.web.rest.ReportMetadata;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BasePatientGridDataResource<T extends BasePatientGridData> extends DelegatingSubResource<T, PatientGrid, PatientGridResource> {
	
	/**
	 * Evaluate the specified {@link PatientGrid} to generate the grid data
	 *
	 * @param parent the {@link PatientGrid} to evaluate
	 * @param context {@link RequestContext} object
	 * @return the generated grid data
	 */
	public abstract ExtendedDataSet evaluate(PatientGrid parent, RequestContext context);
	
	/**
	 * @see DelegatingSubResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("patientGrid", Representation.REF);
		description.addProperty("report");
		description.addProperty("reportMetadata");
		return description;
	}
	
	/**
	 * @see DelegatingSubResource#getParent(Object)
	 */
	@Override
	public PatientGrid getParent(T instance) {
		return instance.getPatientGrid();
	}
	
	/**
	 * @see DelegatingSubResource#doGetAll(Object, RequestContext)
	 */
	@Override
	public PageableResult doGetAll(PatientGrid parent, RequestContext context) throws ResponseException {
		ExtendedDataSet extendedDataSet = evaluate(parent, context);
		SimpleDataSet simpleDataSet = extendedDataSet.getSimpleDataSet();
		List<Map<String, Object>> report = new ArrayList(simpleDataSet.getRows().size());
		simpleDataSet.getRows().stream().forEach(row -> report.add(row.getColumnValuesByKey()));
		T instance = create(new ReportMetadata(extendedDataSet), parent, report);
		
		return new NeedsPaging(Collections.singletonList(instance), context);
	}
	
	protected abstract T create(ReportMetadata reportMetadata, PatientGrid patientGrid, List report);
	
	/**
	 * @see DelegatingSubResource#newDelegate()
	 */
	@Override
	public T newDelegate() {
		throw AgeRangeResource.createReadOnlyException();
	}
	
	/**
	 * @see DelegatingSubResource#save(Object)
	 */
	@Override
	public T save(T delegate) {
		throw AgeRangeResource.createReadOnlyException();
	}
	
	/**
	 * @see DelegatingSubResource#setParent(Object, Object)
	 */
	@Override
	public void setParent(T instance, PatientGrid parent) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see DelegatingSubResource#getByUniqueId(String)
	 */
	@Override
	public T getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see DelegatingSubResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(T delegate, String reason, RequestContext context) throws ResponseException {
		throw AgeRangeResource.createReadOnlyException();
	}
	
	/**
	 * @see DelegatingSubResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(T delegate, RequestContext context) throws ResponseException {
		throw AgeRangeResource.createReadOnlyException();
	}
	
}
