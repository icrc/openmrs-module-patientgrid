package org.openmrs.module.patientgrid.period;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DateRangeTest {

  @Test
  public void dateRange_shouldSerializable() {
    Date d = new Date();
    DateRange dateRange = new DateRange("test", d, d);

    DateRange serialized = SerializationUtils.clone(dateRange);

    assertEquals(dateRange.getOperand(), serialized.getOperand());
    assertEquals(dateRange.getFromInServerTz(), serialized.getFromInServerTz());
    assertEquals(dateRange.getToInServerTz(), serialized.getToInServerTz());
  }

}
