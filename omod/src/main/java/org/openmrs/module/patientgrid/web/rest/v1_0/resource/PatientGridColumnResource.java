package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.NAMESPACE;
import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;

@SubResource(parent = PatientGridResource.class, path = "column", supportedClass = PatientGridColumn.class, supportedOpenmrsVersions = {
        SUPPORTED_VERSIONS })
public class PatientGridColumnResource extends DelegatingSubResource<PatientGridColumn, PatientGrid, PatientGridResource> {
	
	/**
	 * @see DelegatingSubResource#hasTypesDefined()
	 */
	@Override
	public boolean hasTypesDefined() {
		return true;
	}
	
	/**
	 * @see DelegatingSubResource#getResourceName() ()
	 */
	@Override
	protected String getResourceName() {
		return NAMESPACE + "/patientgrid/column";
	}
	
	/**
	 * @see DelegatingSubResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display");
		description.addRequiredProperty("name");
		description.addProperty("description");
		description.addRequiredProperty("datatype");
		if (representation instanceof FullRepresentation) {
			description.addProperty("auditInfo");
		}
		
		return description;
	}
	
	/**
	 * @see DelegatingSubResource#newDelegate()
	 */
	@Override
	public PatientGridColumn newDelegate() {
		return new PatientGridColumn();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addRequiredProperty("datatype");
		description.addProperty("description");
		return description;
	}
	
	@PropertyGetter("display")
	public String getDisplayString(PatientGridColumn delegate) {
		return delegate.getName();
	}
	
	/**
	 * @see DelegatingSubResource#getParent(Object)
	 */
	@Override
	public PatientGrid getParent(PatientGridColumn instance) {
		return instance.getPatientGrid();
	}
	
	/**
	 * @see DelegatingSubResource#setParent(Object, Object)
	 */
	@Override
	public void setParent(PatientGridColumn instance, PatientGrid parent) {
		parent.addColumn(instance);
	}
	
	/**
	 * @see DelegatingSubResource#doGetAll(Object, RequestContext)
	 */
	@Override
	public PageableResult doGetAll(PatientGrid parent, RequestContext context) throws ResponseException {
		List<PatientGridColumn> columns = new ArrayList();
		if (parent != null) {
			for (PatientGridColumn column : parent.getColumns()) {
				columns.add(column);
			}
		}
		
		return new NeedsPaging(columns, context);
	}
	
	/**
	 * @see DelegatingSubResource#getByUniqueId(String)
	 */
	@Override
	public PatientGridColumn getByUniqueId(String uniqueId) {
		return Context.getService(PatientGridService.class).getPatientGridColumnByUuid(uniqueId);
	}
	
	/**
	 * @see DelegatingSubResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(PatientGridColumn delegate, String reason, RequestContext context) throws ResponseException {
		purge(delegate, context);
	}
	
	/**
	 * @see DelegatingSubResource#save(Object)
	 */
	@Override
	public PatientGridColumn save(PatientGridColumn delegate) {
		delegate.getPatientGrid().addColumn(delegate);
		Context.getService(PatientGridService.class).savePatientGrid(delegate.getPatientGrid());
		return delegate;
	}
	
	/**
	 * @see DelegatingSubResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(PatientGridColumn delegate, RequestContext context) throws ResponseException {
		delegate.getPatientGrid().getColumns().remove(delegate);
		Context.getService(PatientGridService.class).savePatientGrid(delegate.getPatientGrid());
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getGETModel(Representation)
	 */
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		model.property("name", new StringProperty());
		model.property("uuid", new StringProperty());
		model.property("datatype", new EnumProperty(ColumnDatatype.class));
		model.property("description", new StringProperty());
		return model;
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getCREATEModel(Representation)
	 */
	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		model.property("name", new StringProperty().required(true));
		model.property("datatype", new EnumProperty(ColumnDatatype.class).required(true));
		model.property("description", new StringProperty());
		return model;
	}
	
}
