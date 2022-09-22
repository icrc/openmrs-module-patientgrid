package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubResource(parent = PatientGridResource.class, path = "filter", supportedClass = PatientGridColumnFilter.class, supportedOpenmrsVersions = {
        SUPPORTED_VERSIONS })
public class PatientGridColumnFilterResource extends DelegatingSubResource<PatientGridColumnFilter, PatientGrid, PatientGridResource> {
	
	/**
	 * @see DelegatingSubResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display");
		description.addRequiredProperty("name");
		description.addRequiredProperty("patientGridColumn");
		description.addRequiredProperty("operand");
		description.addSelfLink();
		if (representation instanceof FullRepresentation) {
			description.addProperty("auditInfo");
		}
		
		return description;
	}
	
	/**
	 * @see DelegatingSubResource#newDelegate()
	 */
	@Override
	public PatientGridColumnFilter newDelegate() {
		return new PatientGridColumnFilter();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addRequiredProperty("patientGridColumn");
		description.addRequiredProperty("operand");
		return description;
	}
	
	@PropertyGetter("display")
	public String getDisplayString(PatientGridColumnFilter delegate) {
		return delegate.getName();
	}
	
	/**
	 * @see DelegatingSubResource#getParent(Object)
	 */
	@Override
	public PatientGrid getParent(PatientGridColumnFilter instance) {
		return instance.getPatientGridColumn().getPatientGrid();
	}
	
	/**
	 * @see DelegatingSubResource#setParent(Object, Object)
	 */
	@Override
	public void setParent(PatientGridColumnFilter instance, PatientGrid parent) {
	}
	
	/**
	 * @see DelegatingSubResource#getByUniqueId(String)
	 */
	@Override
	public PatientGridColumnFilter getByUniqueId(String uniqueId) {
		return Context.getService(PatientGridService.class).getPatientGridColumnFilterByUuid(uniqueId);
	}
	
	/**
	 * @see DelegatingSubResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(PatientGridColumnFilter delegate, String reason, RequestContext context) throws ResponseException {
		purge(delegate, context);
	}
	
	/**
	 * @see DelegatingSubResource#save(Object)
	 */
	@Override
	public PatientGridColumnFilter save(PatientGridColumnFilter delegate) {
		PatientGridColumn column = delegate.getPatientGridColumn();
		column.addFilter(delegate);
		Context.getService(PatientGridService.class).savePatientGrid(column.getPatientGrid());
		return delegate;
	}
	
	/**
	 * @see DelegatingSubResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(PatientGridColumnFilter delegate, RequestContext context) throws ResponseException {
		delegate.getPatientGridColumn().removeFilter(delegate);
		Context.getService(PatientGridService.class).savePatientGrid(delegate.getPatientGridColumn().getPatientGrid());
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getGETModel(Representation)
	 */
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		model.property("name", new StringProperty());
		model.property("uuid", new StringProperty());
		model.property("patientGridColumn", new RefProperty("#/definitions/PatientgridPatientgridColumnGetRef"));
		model.property("operand", new StringProperty());
		return model;
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getCREATEModel(Representation)
	 */
	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		model.property("name", new StringProperty().required(true));
		model.property("patientGridColumn", new StringProperty().required(true).example("uuid"));
		model.property("operand", new StringProperty());
		return model;
	}
	
	private void ensurePatientGridMatch(PatientGridColumnFilter filter) {
		if (filter.getPatientGridColumn().equals(getParent(filter))) {
			throw new GenericRestException("The parent patient grid must match that of the column");
		}
	}
	
	/**
	 * @see DelegatingSubResource#doGetAll(Object, RequestContext)
	 */
	@Override
	public PageableResult doGetAll(PatientGrid parent, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("To view filters on a column, fetch the column itself");
	}
	
}
