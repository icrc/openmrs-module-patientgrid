package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.NAMESPACE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.patientgrid.web.rest.PatientGridReport;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.SubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseUriSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientGridReportController extends BaseRestController {

  @Autowired
  private RestService restService;

  @Autowired
  private BaseUriSetup baseUriSetup;

  /**
   * @param patientGridUuid the uuid of the patient grid
   * @param request         {@link HttpServletRequest} object
   * @param response        {@link HttpServletResponse} object
   * @return SimpleObject
   * @throws ResponseException
   */
  @RequestMapping(value = "/rest/" + NAMESPACE + "/patientgrid/{patientGridUuid}/report", method = GET)
  @ResponseBody
  public SimpleObject evaluate(@PathVariable("patientGridUuid") String patientGridUuid, HttpServletRequest request,
                               HttpServletResponse response) throws ResponseException {

    baseUriSetup.setup(request);

    RequestContext context = RestUtil.getRequestContext(request, response);

    SubResource resource = (SubResource) restService.getResourceBySupportedClass(PatientGridReport.class);

    return resource.getAll(patientGridUuid, context);
  }

}
