package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.AgeAtEncounterPatientGridColumn;
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
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubClassHandler(supportedClass = AgeAtEncounterPatientGridColumn.class, supportedOpenmrsVersions = { SUPPORTED_VERSIONS })
public class AgeAtEncounterPatientGridColumnResource extends BaseDelegatingSubclassHandler<PatientGridColumn, AgeAtEncounterPatientGridColumn> implements DelegatingSubclassHandler<PatientGridColumn, AgeAtEncounterPatientGridColumn> {
	
	/**
	 * @see BaseDelegatingSubclassHandler#getTypeName
	 */
	@Override
	public String getTypeName() {
		return "agecolumn";
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
		BaseDelegatingConverter columnResource = (BaseDelegatingConverter) Context.getService(RestService.class)
		        .getResourceBySupportedClass(PatientGridColumn.class);
		DelegatingResourceDescription description = columnResource.getRepresentationDescription(representation);
		description.addRequiredProperty("encounterType", Representation.REF);
		description.addProperty("convertToAgeRange");
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
