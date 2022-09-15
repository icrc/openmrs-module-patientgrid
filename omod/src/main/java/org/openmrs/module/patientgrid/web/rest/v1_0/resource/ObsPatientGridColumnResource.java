package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.openmrs.module.patientgrid.ObsPatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubClassHandler(supportedClass = ObsPatientGridColumn.class, supportedOpenmrsVersions = { "1.10.*", "1.11.*", "1.12.*",
        "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*", "2.5.*" })
public class ObsPatientGridColumnResource extends BaseDelegatingSubclassHandler<PatientGridColumn, ObsPatientGridColumn> {
	
	/**
	 * @see BaseDelegatingSubclassHandler#getTypeName
	 */
	@Override
	public String getTypeName() {
		return "obs";
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
		DelegatingResourceDescription description = getResource().getRepresentationDescription(representation);
		description.addRequiredProperty("concept");
		description.addRequiredProperty("encounterType");
		return description;
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
