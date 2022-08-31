package org.openmrs.module.patientgrid.web.rest.v1_0.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + PatientGridRestConstants.NAMESPACE + "/patientgrid", method = RequestMethod.GET)
public class PatientGridController extends BaseRestController {
	
	@RequestMapping(value = "/{uuid}/report")
	@ResponseBody
	public Object getPatientGridReport(@PathVariable("uuid") String uuid,
	        @RequestParam(value = "refresh", required = false, defaultValue = "false") boolean refresh) throws Exception {
		
		PatientGridService service = Context.getService(PatientGridService.class);
		PatientGrid patientGrid = service.getPatientGridByUuid(uuid);
		ReportData reportData = service.evaluate(patientGrid);
		SimpleDataSet dataset = (SimpleDataSet) reportData.getDataSets().get("patientData");
		List<Map<String, Object>> report = new ArrayList(dataset.getRows().size());
		dataset.getRows().forEach(row -> {
			report.add(row.getColumnValuesByKey());
		});
		
		SimpleObject so = new SimpleObject();
		so.put("patientGrid", uuid);
		so.put("report", report);
		
		return so;
	}
	
}
