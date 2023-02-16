package org.openmrs.module.patientgrid.period;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.TimeZone;

public class DateRangeParameter {

  private final DateTime fromDateInUserTz;
  private final DateTime toDateInUserTz;

  private final DateTime currentDate;

  private final DateTimeZone serverTimezone;

  public DateRangeParameter(final DateTime fromDate, final DateTime toDate, final DateTime currentDate, final DateTimeZone serverTimezone) {
    this.fromDateInUserTz = fromDate;
    this.toDateInUserTz = toDate;
    this.currentDate = currentDate;
    this.serverTimezone = serverTimezone;
  }

  public DateTime getFromDateInUserTz() {
    return this.fromDateInUserTz;
  }

  public DateTime getToDateInUserTz() {
    return this.toDateInUserTz;
  }

  public DateTime getCurrentDate() {
    return this.currentDate;
  }

  public DateTimeZone getServerTimezone() {
    return this.serverTimezone;
  }

}
