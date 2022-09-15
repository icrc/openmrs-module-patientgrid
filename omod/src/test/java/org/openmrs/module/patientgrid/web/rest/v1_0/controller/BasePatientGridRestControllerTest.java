package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

public abstract class BasePatientGridRestControllerTest extends MainResourceControllerTest {
	
	/**
	 * @see MainResourceControllerTest#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return PatientGridRestConstants.NAMESPACE;
	}
	
}
