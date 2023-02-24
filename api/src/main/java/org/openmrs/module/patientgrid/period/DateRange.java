package org.openmrs.module.patientgrid.period;

import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

public class DateRange {
	
	private final String operand;
	
	private final Date fromInServerTz;
	
	private final Date toInServerTz;
	
	public DateRange(String operand, Date fromInServerTz, Date toInServerTz) {
		this.fromInServerTz = fromInServerTz;
		this.toInServerTz = toInServerTz;
		this.operand = operand;
	}
	
	public String getOperand() {
		return operand;
	}
	
	public String getDateRangeAsString() {
		return ISODateTimeFormat.date().print(fromInServerTz.getTime()) + "_"
		        + ISODateTimeFormat.date().print(toInServerTz.getTime());
	}
	
	public Date getToInServerTz() {
		return toInServerTz;
	}
	
	public Date getFromInServerTz() {
		return fromInServerTz;
	}
	
}
