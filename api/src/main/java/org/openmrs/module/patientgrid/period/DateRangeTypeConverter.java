package org.openmrs.module.patientgrid.period;

import org.joda.time.DateTime;

public abstract class DateRangeTypeConverter {

	public abstract DateRange convert(DateRangeParameter in);

	protected DateTime startOfDay(DateTime in) {
		return in.withTimeAtStartOfDay();
	}

	protected DateTime endOfDay(DateTime in) {
		return in.millisOfDay().withMaximumValue();
	}

	public static DateTime quarterStartFor(DateTime date) {
		return date.withDayOfMonth(1).withMonthOfYear((((date.getMonthOfYear() - 1) / 3) * 3) + 1);
	}

	public static class Today extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime currentDate = in.getCurrentDateInUserTz();
			DateTime startOfDay = startOfDay(currentDate);
			DateTime endOfDay = endOfDay(currentDate);

			return new DateRange(in.getOperand(), startOfDay.toDate(), endOfDay.toDate());
		}
	}

	public static class Yesterday extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime yesterday = in.getCurrentDateInUserTz().minusDays(1);
			return new DateRange(in.getOperand(), startOfDay(yesterday).toDate(), endOfDay(yesterday).toDate());
		}
	}

	public static class CustomDaysInclusive extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime startOfDay = startOfDay(in.getFromDateInUserTz());
			DateTime endOfDay = endOfDay(in.getToDateInUserTz());

			return new DateRange(in.getOperand(), startOfDay.toDate(), endOfDay.toDate());
		}
	}

	public static class LastSevenDays extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime today = in.getCurrentDateInUserTz();
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(today.minusDays(6)).toDate(), endOfDay(today).toDate());
		}
	}

	public static class LastThirtyDays extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime today = in.getCurrentDateInUserTz();
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(today.minusDays(29)).toDate(), endOfDay(today).toDate());
		}
	}

	public static class WeekToDate extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime today = in.getCurrentDateInUserTz();
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(today.withDayOfWeek(1)).toDate(), endOfDay(today).toDate());
		}
	}

	public static class MonthToDate extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime today = in.getCurrentDateInUserTz();
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(today.withDayOfMonth(1)).toDate(), endOfDay(today).toDate());
		}
	}

	public static class QuarterToDate extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime today = in.getCurrentDateInUserTz();
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(quarterStartFor(today)).toDate(), endOfDay(today).toDate());
		}
	}

	public static class YearToDate extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime today = in.getCurrentDateInUserTz();
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(today.withDayOfYear(1)).toDate(), endOfDay(today).toDate());
		}
	}

	public static class PreviousWeek extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime previousWeek = in.getCurrentDateInUserTz().minusWeeks(1);
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(previousWeek.withDayOfWeek(1)).toDate(),
			        endOfDay(previousWeek.withDayOfWeek(7)).toDate());
		}
	}

	public static class PreviousMonth extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime startPreviousMonth = in.getCurrentDateInUserTz().minusMonths(1).withDayOfMonth(1);
			//6 interval
			return new DateRange(in.getOperand(), startOfDay(startPreviousMonth).toDate(),
			        endOfDay(startPreviousMonth.dayOfMonth().withMaximumValue()).toDate());
		}
	}

	public static class PreviousQuarter extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime startOfCurrentQuarter = quarterStartFor(in.getCurrentDateInUserTz());

			return new DateRange(in.getOperand(), startOfDay(startOfCurrentQuarter.minusMonths(3)).toDate(),
			        endOfDay(startOfCurrentQuarter.minusDays(1)).toDate());
		}
	}

	public static class PreviousYear extends DateRangeTypeConverter {

		@Override
		public DateRange convert(DateRangeParameter in) {
			DateTime previousYear = in.getCurrentDateInUserTz().minusYears(1);
			return new DateRange(in.getOperand(), startOfDay(previousYear.dayOfYear().withMinimumValue()).toDate(),
			        endOfDay(previousYear.dayOfYear().withMaximumValue()).toDate());
		}
	}
}
