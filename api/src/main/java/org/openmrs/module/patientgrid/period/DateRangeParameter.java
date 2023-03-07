package org.openmrs.module.patientgrid.period;

import org.joda.time.DateTime;
import org.openmrs.api.context.Context;

import java.util.Locale;

public class DateRangeParameter {
	
	private final String operand;
	
	private final DateTime fromDateInUserTz;
	
	private final DateTime toDateInUserTz;
	
	private final DateTime currentDateInUserTz;
	
	public DateRangeParameter(String operand, DateTime fromDateInUserTz, DateTime toDateInUserTz,
	    DateTime currentDateInUserTz) {
		this.fromDateInUserTz = fromDateInUserTz;
		this.toDateInUserTz = toDateInUserTz;
		this.currentDateInUserTz = currentDateInUserTz;
		this.operand = operand;
	}
	
	public String getOperand() {
		return operand;
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
