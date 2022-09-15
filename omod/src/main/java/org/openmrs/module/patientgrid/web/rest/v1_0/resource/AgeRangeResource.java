package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants;
import org.openmrs.module.reporting.common.AgeRange;
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

@Resource(name = PatientGridRestConstants.NAMESPACE
        + "/agerange", supportedClass = AgeRange.class, supportedOpenmrsVersions = { "1.10.*", "1.11.*", "1.12.*", "2.0.*",
                "2.1.*", "2.2.*", "2.3.*", "2.4.*", "2.5.*" })
public class AgeRangeResource extends DelegatingCrudResource<AgeRange> {
	
	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("minAge");
		description.addProperty("maxAge");
		description.addProperty("label");
		description.addProperty("display");
		return description;
	}
	
	/**
	 * @see DelegatingCrudResource#doGetAll(RequestContext)
	 */
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging(PatientGridUtils.getAgeRanges(), context);
	}
	
	@PropertyGetter("display")
	public String getDisplayString(AgeRange delegate) {
		return delegate.getLabel();
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
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
	/**
	 * @see DelegatingCrudResource#save(Object)
	 */
	@Override
	public AgeRange save(AgeRange ageRange) {
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
	/**
	 * @see DelegatingCrudResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(AgeRange ageRange, String s, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
	/**
	 * @see DelegatingCrudResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(AgeRange ageRange, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("read-only resource");
	}
	
}
