package org.openmrs.module.patientgrid.period;

import org.joda.time.DateTime;

import java.util.Date;

public abstract class DateRangeTypeConverter {

  public abstract DateRange convert(DateRangeParameter in);

  public static class Today extends DateRangeTypeConverter {

    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class CustomDaysInclusive extends DateRangeTypeConverter {

    @Override
    public DateRange convert(DateRangeParameter in) {
      DateTime startOfDay = in.getFromDateInUserTz().withTimeAtStartOfDay();
      DateTime endOfDay = in.getToDateInUserTz().millisOfDay().withMaximumValue();

      Date from = startOfDay.toLocalDateTime().toDate(in.getServerTimezone().toTimeZone());
      Date to = endOfDay.toLocalDateTime().toDate(in.getServerTimezone().toTimeZone());

      return new DateRange(from, to);
    }
  }

  public static class LastSevenDays extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class LastThirtyDays extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class WeekToDate extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class MonthToDate extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class QuarterToDate extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class YearToDate extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class PreviousWeek extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class PreviousMonth extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class PreviousQuarter extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }

  public static class PreviousYear extends DateRangeTypeConverter {
    @Override
    public DateRange convert(DateRangeParameter in) {
      return null;
    }
  }
}
