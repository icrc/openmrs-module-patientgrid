package org.openmrs.module.patientgrid.converter;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Custom {@link Age} {@link DataConverter} that returns the age value in years
 */
public class PatientGridAgeConverter implements DataConverter {

  /**
   * @see DataConverter#convert(Object)
   */
  @Override
  public Object convert(Object original) {
    if (original != null) {
      Age age = (Age) original;
      return age.getFullYears();
    }

    return null;
  }

  /**
   * @see DataConverter#getInputDataType()
   */
  @Override
  public Class<?> getInputDataType() {
    return Age.class;
  }

  /**
   * @see DataConverter#getDataType()
   */
  @Override
  public Class<?> getDataType() {
    return Integer.class;
  }

}
