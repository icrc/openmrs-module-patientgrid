package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.openmrs.module.patientgrid.AgeAtEncounterPatientGridColumn;
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
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubClassHandler(supportedClass = AgeAtEncounterPatientGridColumn.class, supportedOpenmrsVersions = { "1.10.*", "1.11.*",
        "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*", "2.5.*" })
public class AgeAtEncounterPatientGridColumnResource extends BaseDelegatingSubclassHandler<PatientGridColumn, AgeAtEncounterPatientGridColumn> {
	
	/**
	 * @see BaseDelegatingSubclassHandler#getTypeName
	 */
	@Override
	public String getTypeName() {
		return "age";
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#newDelegate
	 */
	@Override
	public AgeAtEncounterPatientGridColumn newDelegate() {
		return new AgeAtEncounterPatientGridColumn();
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getRepresentationDescription
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = getResource().getRepresentationDescription(representation);
		description.addRequiredProperty("encounterType");
		description.addProperty("convertToAgeRange");
		return description;
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getCreatableProperties
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = getResource().getCreatableProperties();
		description.addRequiredProperty("encounterType");
		description.addProperty("convertToAgeRange");
		return description;
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getGETModel
	 */
	@Override
	public Model getGETModel(Representation representation) {
		ModelImpl model = (ModelImpl) getResource().getGETModel(representation);
		model.property("encounterType", new RefProperty("#/definitions/EncountertypeGetRef"));
		model.property("convertToAgeRange", new BooleanProperty());
		return model;
	}
	
	/**
	 * @see BaseDelegatingSubclassHandler#getCREATEModel
	 */
	@Override
	public Model getCREATEModel(Representation representation) {
		ModelImpl model = (ModelImpl) getResource().getCREATEModel(representation);
		model.property("encounterType", new StringProperty().required(true).example("uuid"));
		model.property("convertToAgeRange", new BooleanProperty()._default(false));
		model.required("encounterType");
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
