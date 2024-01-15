package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainSubResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rest/" + PatientGridRestConstants.NAMESPACE)
public class PatientGridSubResourceController extends MainSubResourceController {

	/**
	 * @see MainSubResourceController#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return PatientGridRestConstants.NAMESPACE;
	}

}
