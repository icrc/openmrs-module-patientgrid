package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.NAMESPACE;
import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.reporting.common.Age.Unit;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.StringProperty;

@Resource(name = NAMESPACE + "/agerange", supportedClass = AgeRange.class, supportedOpenmrsVersions = { SUPPORTED_VERSIONS })
public class AgeRangeResource extends DelegatingCrudResource<AgeRange> {
	
	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("minAge");
		description.addProperty("minAgeUnit");
		description.addProperty("maxAge");
		description.addProperty("maxAgeUnit");
		description.addProperty("label");
		description.addProperty(PatientGridConstants.PROPERTY_DISPLAY);
		return description;
	}
	
	/**
	 * @see DelegatingCrudResource#doGetAll(RequestContext)
	 */
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging(PatientGridUtils.getAgeRanges(), context);
	}
	
	@PropertyGetter(PatientGridConstants.PROPERTY_DISPLAY)
	public String getDisplayString(AgeRange delegate) {
		return delegate.getLabel();
	}
	
	/**
	 * @see DelegatingCrudResource#getGETModel(Representation)
	 */
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		model.property("minAge", new IntegerProperty());
		model.property("minAgeUnit", new EnumProperty(Unit.class));
		model.property("maxAge", new IntegerProperty());
		model.property("maxAgeUnit", new EnumProperty(Unit.class));
		model.property("label", new StringProperty());
		model.property(PatientGridConstants.PROPERTY_DISPLAY, new StringProperty());
		return model;
	}
	
	/**
	 * @see DelegatingCrudResource#getByUniqueId(String)
	 */
	@Override
	public AgeRange getByUniqueId(String s) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public AgeRange newDelegate() {
		throw createReadOnlyException();
	}
	
	/**
	 * @see DelegatingCrudResource#save(Object)
	 */
	@Override
	public AgeRange save(AgeRange ageRange) {
		throw createReadOnlyException();
	}
	
	public static ResourceDoesNotSupportOperationException createReadOnlyException() {
		return new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
	/**
	 * @see DelegatingCrudResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(AgeRange ageRange, String s, RequestContext requestContext) throws ResponseException {
		throw createReadOnlyException();
	}
	
	/**
	 * @see DelegatingCrudResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(AgeRange ageRange, RequestContext requestContext) throws ResponseException {
		throw createReadOnlyException();
	}
	
}
