package org.openmrs.module.patientgrid.period;

import org.joda.time.DateTime;

public class DateRangeParameter {
	
	private final DateTime fromDateInUserTz;
	
	private final DateTime toDateInUserTz;
	
	private final DateTime currentDateInUserTz;
	
	public DateRangeParameter(DateTime fromDateInUserTz, DateTime toDateInUserTz, DateTime currentDateInUserTz) {
		this.fromDateInUserTz = fromDateInUserTz;
		this.toDateInUserTz = toDateInUserTz;
		this.currentDateInUserTz = currentDateInUserTz;
	}
	
	public DateTime getFromDateInUserTz() {
		return this.fromDateInUserTz;
	}
	
	public DateTime getToDateInUserTz() {
		return this.toDateInUserTz;
	}
	
	public DateTime getCurrentDateInUserTz() {
		return this.currentDateInUserTz;
	}
	
}
