package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype.NAME;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientGridTest {

  @Test
  public void getObsColumns_shouldGetAllTheObsColumnsInTheGrid() {
    final PatientGrid patientGrid = new PatientGrid();
    final PatientGridColumn weightColumn = new ObsPatientGridColumn();
    final PatientGridColumn heightColumn = new ObsPatientGridColumn();
    patientGrid.addColumn(new PatientGridColumn(null, NAME));
    patientGrid.addColumn(weightColumn);
    patientGrid.addColumn(heightColumn);
    Assert.assertEquals(2, patientGrid.getObsColumns().size());
    Assert.assertTrue(patientGrid.getObsColumns().contains(weightColumn));
    Assert.assertTrue(patientGrid.getObsColumns().contains(heightColumn));
  }

}
