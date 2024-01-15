package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.EncounterDatePatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubClassHandler(supportedClass = EncounterDatePatientGridColumn.class, supportedOpenmrsVersions = { SUPPORTED_VERSIONS })
public class EncounterDatePatientGridColumnResource extends BaseDelegatingSubclassHandler<PatientGridColumn, EncounterDatePatientGridColumn> implements DelegatingSubclassHandler<PatientGridColumn, EncounterDatePatientGridColumn> {

	/**
	 * @see BaseDelegatingSubclassHandler#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "encounterdatecolumn";
	}

	/**
	 * @see BaseDelegatingSubclassHandler#newDelegate()
	 */
	@Override
	public EncounterDatePatientGridColumn newDelegate() {
		return new EncounterDatePatientGridColumn();
	}

	/**
	 * @see BaseDelegatingSubclassHandler#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = getSuperclassResource().getRepresentationDescription(representation);
		if (representation instanceof DefaultRepresentation || representation instanceof FullRepresentation) {
			description.addRequiredProperty(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE, Representation.REF);
		}

		return description;
	}

	@PropertyGetter(PatientGridConstants.PROPERTY_DISPLAY)
	public String getDisplayString(PatientGridColumn delegate) {
		return getSuperclassResource().getDisplayString(delegate);
	}

	@PropertySetter(PatientGridConstants.PROP_FILTERS)
	public void setFilters(PatientGridColumn column, PatientGridColumnFilter... filters) {
		getSuperclassResource().setFilters(column, filters);
	}

	/**
	 * @see BaseDelegatingSubclassHandler#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = getResource().getCreatableProperties();
		description.addRequiredProperty(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE);
		return description;
	}

	/**
	 * @see BaseDelegatingSubclassHandler#getGETModel(Representation)
	 */
	@Override
	public Model getGETModel(Representation representation) {
		ModelImpl model = (ModelImpl) getResource().getGETModel(representation);
		model.property(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE, new RefProperty("#/definitions/EncountertypeGetRef"));
		return model;
	}

	/**
	 * @see BaseDelegatingSubclassHandler#getCREATEModel(Representation)
	 */
	@Override
	public Model getCREATEModel(Representation representation) {
		ModelImpl model = (ModelImpl) getResource().getCREATEModel(representation);
		model.property(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE, new StringProperty().required(true).example("uuid"));
		return model;
	}

	/**
	 * @see BaseDelegatingSubclassHandler#getAllByType(RequestContext)
	 */
	@Override
	public PageableResult getAllByType(RequestContext requestContext) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}

	private PatientGridColumnResource getSuperclassResource() {
		return (PatientGridColumnResource) Context.getService(RestService.class)
		        .getResourceBySupportedClass(PatientGridColumn.class);
	}

}
