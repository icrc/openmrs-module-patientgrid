package org.openmrs.module.patientgrid.period;

import jdk.nashorn.internal.AssertsEnabled;
import junit.framework.TestCase;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.patientgrid.PatientGridUtils;

import java.text.ParseException;
import java.util.Date;

public class DateRangeConverterTest {

//  {"fromDate":"2023-02-13 00:00:00","toDate":"2023-02-14 00:00:00"}

  private String createJson(String code, String from, String to) {
    return String.format("{\"code\":\"%s\",\"fromDate\":\"%s 00:00:00\",\"toDate\":\"%s 00:00:00\"}", code, from, to);
  }

  private String createJson(String code) {
    return String.format("{\"code\":\"%s\",\"fromDate\":\"\",\"toDate\":\"\"}", code);
  }

  @Test
  public void createJson_shouldReturnJson() {
    Assert.assertEquals("{\"code\":\"today\",\"fromDate\":\"\",\"toDate\":\"\"}", createJson("today"));
    Assert.assertEquals("{\"code\":\"customDaysInclusive\",\"fromDate\":\"2022-01-02 00:00:00\",\"toDate\":\"2022-01-03 00:00:00\"}", createJson("customDaysInclusive", "2022-01-02", "2022-01-03"));

  }

  private String formatDate(Date in) {
    return PatientGridConstants.DATETIME_FORMAT.format(in);
  }

  @Test
  public void creationDateRangeConverter_shouldParseTimezones() throws ParseException {

    DateRangeConverter converter = new DateRangeConverter("Europe/Paris", "UTC");

    Assert.assertEquals(DateTimeZone.UTC, converter.getServerTimeZone());
    Assert.assertEquals("Europe/Paris", converter.getUserTimeZone().getID());
  }

    @Test
  public void convert_shouldReturnSpecificDateRange() throws ParseException {
    DateRangeConverter converter = new DateRangeConverter("Europe/Paris", "UTC");


    DateRange customDaysInclusive = converter.convert(createJson("customDaysInclusive", "2023-01-10", "2023-01-11"), null);

    Assert.assertEquals("2023-01-10 00:00:00+01:00", formatDate(customDaysInclusive.getFromInServerTz()));
    Assert.assertEquals("2023-01-11 23:59:59+01:00", formatDate(customDaysInclusive.getToInServerTz()));

  }

}