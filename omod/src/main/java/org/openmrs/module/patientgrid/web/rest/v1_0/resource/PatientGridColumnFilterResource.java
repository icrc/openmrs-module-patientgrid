package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.*;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectMismatchException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsUtil;

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
		if (representation instanceof DefaultRepresentation || representation instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty(PatientGridConstants.PROPERTY_DISPLAY);
			description.addRequiredProperty("name");
			description.addRequiredProperty(PatientGridConstants.PROPERTY_COLUMN, Representation.REF);
			description.addRequiredProperty(PatientGridConstants.PROPERTY_OPERAND);
			description.addSelfLink();
			if (representation instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} else {
				description.addProperty("auditInfo");
			}
			
			return description;
		}
		
		return null;
	}
	
	/**
	 * @see DelegatingSubResource#newDelegate()
	 */
	@Override
	public PatientGridColumnFilter newDelegate() {
		return new PatientGridColumnFilter();
	}
	
	/**
	 * @see DelegatingSubResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addRequiredProperty(PatientGridConstants.PROPERTY_COLUMN);
		description.addRequiredProperty(PatientGridConstants.PROPERTY_OPERAND);
		return description;
	}
	
	/**
	 * @see DelegatingSubResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getUpdatableProperties();
		description.removeProperty(PatientGridConstants.PROPERTY_COLUMN);
		return description;
	}
	
	@PropertyGetter(PatientGridConstants.PROPERTY_COLUMN)
	public PatientGridColumn getColumn(PatientGridColumnFilter delegate) {
		return delegate.getPatientGridColumn();
	}
	
	@PropertySetter(PatientGridConstants.PROPERTY_COLUMN)
	public void setColumn(PatientGridColumnFilter delegate, PatientGridColumn column) {
		delegate.setPatientGridColumn(column);
	}
	
	@PropertyGetter(PatientGridConstants.PROPERTY_DISPLAY)
	public String getDisplayString(PatientGridColumnFilter delegate) {
		try {
			return delegate.getPatientGridColumn().getDatatype().getDisplayer().getDisplayString(delegate,
			    Context.getLocale());
		}
		catch (Exception e) {
			log.warn("Can't get display mame for PatientGridColumnFilter uuid" + delegate.getUuid(), e);
		}
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
	 * @see #setColumn(PatientGridColumnFilter, PatientGridColumn)
	 * @see DelegatingSubResource#setParent(Object, Object)
	 */
	@Override
	public void setParent(PatientGridColumnFilter instance, PatientGrid parent) {
		//The parent is the column which is already set on the filter by the framework via the column resource property
	}
	
	/**
	 * @see DelegatingSubResource#getByUniqueId(String)
	 */
	@Override
	public PatientGridColumnFilter getByUniqueId(String uniqueId) {
		return Context.getService(PatientGridService.class).getPatientGridColumnFilterByUuid(uniqueId);
	}
	
	/**
	 * @see DelegatingSubResource#retrieve(String, String, RequestContext)
	 */
	@Override
	public Object retrieve(String parentUniqueId, String uuid, RequestContext context) throws ResponseException {
		ensurePatientGridsMatch(parentUniqueId, uuid);
		return super.retrieve(parentUniqueId, uuid, context);
	}
	
	/**
	 * @see DelegatingSubResource#doGetAll(Object, RequestContext)
	 */
	@Override
	public PageableResult doGetAll(PatientGrid parent, RequestContext context) throws ResponseException {
		List<PatientGridColumnFilter> filters = new ArrayList();
		if (parent != null) {
			parent.getColumns().stream().forEach(c -> c.getFilters().forEach(f -> filters.add(f)));
		}
		
		return new NeedsPaging(filters, context);
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
	 * @see DelegatingSubResource#create(String, SimpleObject, RequestContext)
	 */
	@Override
	public Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException {
		String columnUuid = post.get(PatientGridConstants.PROPERTY_COLUMN);
		PatientGridColumnResource columnResource = (PatientGridColumnResource) Context.getService(RestService.class)
		        .getResourceBySupportedClass(PatientGridColumn.class);
		PatientGridColumn column = columnResource.getByUniqueId(columnUuid);
		if (column == null) {
			throw new ObjectNotFoundException();
		}
		
		if (!OpenmrsUtil.nullSafeEquals(column.getPatientGrid().getUuid(), parentUniqueId)) {
			throw new ObjectMismatchException(parentUniqueId + " does not match that of the column", null);
		}
		
		return super.create(parentUniqueId, post, context);
	}
	
	/**
	 * @see DelegatingSubResource#update(String, String, SimpleObject, RequestContext)
	 */
	@Override
	public Object update(String parentUniqueId, String uuid, SimpleObject propertiesToUpdate, RequestContext context)
	        throws ResponseException {
		ensurePatientGridsMatch(parentUniqueId, uuid);
		return super.update(parentUniqueId, uuid, propertiesToUpdate, context);
	}
	
	/**
	 * @see DelegatingSubResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(PatientGridColumnFilter delegate, String reason, RequestContext context) throws ResponseException {
		purge(delegate, context);
	}
	
	/**
	 * @see DelegatingSubResource#delete(Object, String, RequestContext)
	 */
	@Override
	public void delete(String parentUniqueId, String uuid, String reason, RequestContext context) throws ResponseException {
		ensurePatientGridsMatch(parentUniqueId, uuid);
		super.delete(parentUniqueId, uuid, reason, context);
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
	 * @see DelegatingSubResource#purge(String, String, RequestContext)
	 */
	@Override
	public void purge(String parentUniqueId, String uuid, RequestContext context) throws ResponseException {
		ensurePatientGridsMatch(parentUniqueId, uuid);
		super.purge(parentUniqueId, uuid, context);
	}
	
	/**
	 * @see DelegatingSubResource#getGETModel(Representation)
	 */
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		model.property("name", new StringProperty());
		model.property("uuid", new StringProperty());
		model.property(PatientGridConstants.PROPERTY_COLUMN, new RefProperty("#/definitions/PatientgridPatientgridColumnGetRef"));
		model.property(PatientGridConstants.PROPERTY_OPERAND, new StringProperty());
		return model;
	}
	
	/**
	 * @see DelegatingSubResource#getCREATEModel(Representation)
	 */
	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		model.property("name", new StringProperty().required(true));
		model.property(PatientGridConstants.PROPERTY_COLUMN, new StringProperty().required(true).example("uuid"));
		model.property(PatientGridConstants.PROPERTY_OPERAND, new StringProperty());
		return model;
	}
	
	/**
	 * @see DelegatingSubResource#getUPDATEModel(Representation)
	 */
	@Override
	public Model getUPDATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		model.property("name", new StringProperty().required(true));
		model.property(PatientGridConstants.PROPERTY_OPERAND, new StringProperty());
		return model;
	}
	
	/**
	 * Sanity check to ensure the parent grid and that of the filter column match
	 *
	 * @param parentUniqueId the patient grid uuid
	 * @param uuid filter uuid
	 */
	private void ensurePatientGridsMatch(String parentUniqueId, String uuid) {
		PatientGridColumnFilter filter = getByUniqueId(uuid);
		if (filter == null) {
			throw new ObjectNotFoundException();
		}
		
		if (!OpenmrsUtil.nullSafeEquals(filter.getPatientGridColumn().getPatientGrid().getUuid(), parentUniqueId)) {
			throw new ObjectMismatchException(parentUniqueId + " does not match that of the column", null);
		}
	}
	
}
