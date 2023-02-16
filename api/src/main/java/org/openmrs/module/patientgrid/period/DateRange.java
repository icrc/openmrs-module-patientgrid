package org.openmrs.module.patientgrid.period;

import java.util.Date;

public class DateRange {

  private Date fromInServerTz;

  private Date toInServerTz;

  public DateRange(Date fromInServerTz, Date toInServerTz) {
    this.fromInServerTz = fromInServerTz;
    this.toInServerTz = toInServerTz;
  }

  public Date getToInServerTz() {
    return toInServerTz;
  }


  public Date getFromInServerTz() {
    return fromInServerTz;
  }

}
