package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AgeRangeResourceTest extends BaseDelegatingResourceTest<AgeRangeResource, AgeRange> {
	
	private static final Integer MIN_AGE = 18;
	
	private static final Integer MAX_AGE = 45;
	
	private static final String LABEL = "18-45";
	
	@Override
	public AgeRange newObject() {
		return new AgeRange(MIN_AGE, null, MAX_AGE, null, LABEL);
	}
	
	@Override
	public String getDisplayProperty() {
		return LABEL;
	}
	
	@Override
	public String getUuidProperty() {
		return null;
	}
	
	private void validateAgeRangeRepresentation() {
		assertPropEquals("minAge", MIN_AGE);
		assertPropEquals("maxAge", MAX_AGE);
		assertPropEquals("label", LABEL);
		assertPropEquals(PatientGridConstants.PROPERTY_DISPLAY, LABEL);
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		validateAgeRangeRepresentation();
	}
	
	@Override
	public void validateDefaultRepresentation() {
		validateAgeRangeRepresentation();
	}
	
	@Override
	public void validateFullRepresentation() {
		validateAgeRangeRepresentation();
	}
	
}
