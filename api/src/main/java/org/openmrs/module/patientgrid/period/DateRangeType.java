package org.openmrs.module.patientgrid.period;

public enum DateRangeType {
	
	TODAY(new DateRangeTypeConverter.Today()),
	YESTERDAY(new DateRangeTypeConverter.Yesterday()),
	LASTSEVENDAYS(new DateRangeTypeConverter.LastSevenDays()),
	LASTTHIRTYDAYS(new DateRangeTypeConverter.LastThirtyDays()),
	WEEKTODATE(new DateRangeTypeConverter.WeekToDate()),
	MONTHTODATE(new DateRangeTypeConverter.MonthToDate()),
	QUARTERTODATE(new DateRangeTypeConverter.QuarterToDate()),
	YEARTODATE(new DateRangeTypeConverter.YearToDate()),
	PREVIOUSWEEK(new DateRangeTypeConverter.PreviousWeek()),
	PREVIOUSMONTH(new DateRangeTypeConverter.PreviousMonth()),
	PREVIOUSQUARTER(new DateRangeTypeConverter.PreviousQuarter()),
	
	PREVIOUSYEAR(new DateRangeTypeConverter.PreviousYear()),
	
	CUSTOMDAYSINCLUSIVE(new DateRangeTypeConverter.CustomDaysInclusive());
	
	private final DateRangeTypeConverter converter;
	
	DateRangeType(DateRangeTypeConverter converter) {
		this.converter = converter;
	}
	
	public DateRangeTypeConverter getConverter() {
		return this.converter;
	}
}
