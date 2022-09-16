package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.ObsPatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubClassHandler(supportedClass = ObsPatientGridColumn.class, supportedOpenmrsVersions = { SUPPORTED_VERSIONS })
public class ObsPatientGridColumnResource extends BaseDelegatingSubclassHandler<PatientGridColumn, ObsPatientGridColumn> implements DelegatingSubclassHandler<PatientGridColumn, ObsPatientGridColumn> {
	
	/**
	 * @see BaseDelegatingSubclassHandler#getTypeName
	 */
	@Override
	public String getTypeName() {
		return "obscolumn";
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#newDelegate
	 */
	@Override
	public ObsPatientGridColumn newDelegate() {
		return new ObsPatientGridColumn();
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getRepresentationDescription
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		BaseDelegatingConverter columnResource = (BaseDelegatingConverter) Context.getService(RestService.class)
		        .getResourceBySupportedClass(PatientGridColumn.class);
		DelegatingResourceDescription description = columnResource.getRepresentationDescription(representation);
		description.addRequiredProperty("concept", Representation.REF);
		description.addRequiredProperty("encounterType", Representation.REF);
		return description;
	}
	
	@PropertyGetter("display")
	public String getDisplayString(PatientGridColumn delegate) {
		return delegate.getName();
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getCreatableProperties
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = getResource().getCreatableProperties();
		description.addRequiredProperty("concept");
		description.addRequiredProperty("encounterType");
		return description;
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getGETModel
	 */
	@Override
	public Model getGETModel(Representation representation) {
		ModelImpl model = (ModelImpl) getResource().getGETModel(representation);
		model.property("concept", new RefProperty("#/definitions/ConceptGetRef"));
		model.property("encounterType", new RefProperty("#/definitions/EncountertypeGetRef"));
		return model;
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getCREATEModel
	 */
	@Override
	public Model getCREATEModel(Representation representation) {
		ModelImpl model = (ModelImpl) getResource().getCREATEModel(representation);
		model.property("concept", new StringProperty().required(true).example("uuid"));
		model.property("encounterType", new StringProperty().required(true).example("uuid"));
		return model;
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getAllByType
	 */
	@Override
	public PageableResult getAllByType(RequestContext requestContext) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
}
