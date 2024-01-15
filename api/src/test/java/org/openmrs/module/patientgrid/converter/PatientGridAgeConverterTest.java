package org.openmrs.module.patientgrid.converter;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.reporting.common.Age;

public class PatientGridAgeConverterTest {

  private PatientGridAgeConverter converter = new PatientGridAgeConverter();

  @Test
  public void convert_shouldReturnTheAgeInYears() throws Exception {
    Date birthDate = PatientGridConstants.DATE_FORMAT.parse("2000-05-05");
    Date currentDate = PatientGridConstants.DATE_FORMAT.parse("2020-06-06");
    Assert.assertEquals(20, converter.convert(new Age(birthDate, currentDate)));
  }

  @Test
  public void convert_shouldReturnNullForANullValue() {
    Assert.assertNull(converter.convert(null));
  }

}
