package org.openmrs.module.patientgrid.period;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;
import org.openmrs.api.APIException;
import org.openmrs.module.patientgrid.PatientGridUtils;

import java.io.IOException;
import java.util.Map;
import java.util.TimeZone;

public class DateRangeConverter {

  private final DateTimeZone userTimeZone;
  private final DateTimeZone serverTimeZone;

  private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
      .append(ISODateTimeFormat.date())
      .appendLiteral(' ')
      .append(ISODateTimeFormat.hourMinuteSecond())
      .toFormatter();

  public DateRangeConverter(String userTimeZone, String serverTimeZone) {

    this.userTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(userTimeZone));
    this.serverTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(serverTimeZone));
  }

  public DateTimeZone getServerTimeZone() {
    return this.serverTimeZone;
  }

  public DateTimeZone getUserTimeZone() {
    return this.userTimeZone;
  }

  private DateTime getDateForUser(Map map, String key) throws APIException {
    String asString = (String) map.get(key);
    if (StringUtils.isBlank(asString)) {
      return null;
    }
    try {
      DateTime dateTime = DateTime.parse(asString, FORMATTER);
      return dateTime.withZoneRetainFields(userTimeZone);
    } catch (IllegalArgumentException e) {
      throw new APIException("Can't parse as Date:" + asString, e);
    }
  }

  private DateRangeType getRangeType(String type) {
    if (StringUtils.isNotBlank(type)) {
      try {
        DateRangeType rangeType = DateRangeType.valueOf(type.toUpperCase());
        return rangeType;
      } catch (IllegalArgumentException e) {
        return DateRangeType.CUSTOMDAYSINCLUSIVE;
      }
    }
    return DateRangeType.CUSTOMDAYSINCLUSIVE;
  }


  public DateRange convert(String in) throws APIException {
    return convert(in, DateTime.now());
  }

  public DateRange convert(String in, DateTime currentDate) throws APIException {
    Map map = null;
    try {
      map = PatientGridUtils.MAPPER.readValue(in, Map.class);
    } catch (IOException e) {
      throw new APIException("Can't parse TimeRange: " + in, e);
    }
    final String type = (String) map.get("type");
    final DateTime fromDate = getDateForUser(map, "fromDate");
    final DateTime toDate = getDateForUser(map, "toDate");
    DateRangeParameter parameter = new DateRangeParameter(fromDate, toDate, currentDate, serverTimeZone);
    DateRangeType rangeType = getRangeType(type);
    return rangeType.getConverter().convert(parameter);

  }
}
