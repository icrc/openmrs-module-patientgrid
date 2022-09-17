package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.NAMESPACE;
import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.RefProperty;

@Resource(name = NAMESPACE + "/patientgrid", supportedClass = PatientGrid.class, supportedOpenmrsVersions = {
        SUPPORTED_VERSIONS })
public class PatientGridResource extends MetadataDelegatingCrudResource<PatientGrid> {
	
	/**
	 * @see MetadataDelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (description == null) {
			return null;
		}
		
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description.addProperty("owner", Representation.REF);
			if (rep instanceof FullRepresentation) {
				description.addRequiredProperty("columns", Representation.DEFAULT);
				description.addProperty("auditInfo");
			}
		}
		
		return description;
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addRequiredProperty("columns");
		description.addProperty("owner");
		return description;
	}
	
	@PropertySetter("columns")
	public void setColumns(PatientGrid instance, PatientGridColumn... columns) {
		for (PatientGridColumn column : columns) {
			instance.addColumn(column);
		}
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#newDelegate()
	 */
	@Override
	public PatientGrid newDelegate() {
		return new PatientGrid();
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getByUniqueId(String)
	 */
	@Override
	public PatientGrid getByUniqueId(String s) {
		return Context.getService(PatientGridService.class).getPatientGridByUuid(s);
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#save(Object)
	 */
	@Override
	public PatientGrid save(PatientGrid patientGrid) {
		return Context.getService(PatientGridService.class).savePatientGrid(patientGrid);
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#doGetAll(RequestContext)
	 */
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging(Context.getService(PatientGridService.class).getPatientGrids(context.getIncludeAll()),
		        context);
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getGETModel(Representation)
	 */
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model.property("owner", new RefProperty("#/definitions/UserGetRef"));
			if (rep instanceof FullRepresentation) {
				model.property("columns",
				    new ArrayProperty(new RefProperty("#/definitions/PatientgridPatientgridColumnGet")));
			}
		}
		
		return model;
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#getCREATEModel(Representation)
	 */
	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		model.required("name");
		model.property("columns", new ArrayProperty(new RefProperty("#/definitions/PatientgridPatientgridColumnCreate")));
		model.required("columns");
		model.property("owner", new RefProperty("#/definitions/UserGetRef"));
		return model;
	}
	
	/**
	 * @see MetadataDelegatingCrudResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(PatientGrid patientGrid, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
}
