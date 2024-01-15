package org.openmrs.module.patientgrid.web.rest;

import org.openmrs.module.patientgrid.PatientGrid;

import java.util.List;
import java.util.Map;

public class PatientGridDownload extends BasePatientGridData {

  public PatientGridDownload(ReportMetadata reportMetadata, PatientGrid patientGrid, List<Map<String, Object>> report) {
    super(reportMetadata, patientGrid, report);
  }

}
