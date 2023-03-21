package org.openmrs.module.patientgrid.web.rest.v1_0;

import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;

public final class PatientGridRestConstants {
	
	private PatientGridRestConstants() {
		//utility class
	}
	
	public static final String NAMESPACE = RestConstants.VERSION_1 + "/" + PatientGridConstants.MODULE_ID;
	
	public static final String SUPPORTED_VERSIONS = "2.3.* - 2.5.*";
	
	public static final String PARAM_REFRESH = "refresh";
	
}
