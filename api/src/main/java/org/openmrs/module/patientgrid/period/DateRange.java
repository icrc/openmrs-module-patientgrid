package org.openmrs.module.patientgrid.period;

import java.io.Serializable;
import java.util.Date;

public class DateRange implements Serializable {

	public static final long serialVersionUID = 1L;

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
		return fromInServerTz.getTime() + "-" + toInServerTz.getTime();
	}

	public Date getToInServerTz() {
		return toInServerTz;
	}

	public Date getFromInServerTz() {
		return fromInServerTz;
	}

	public long getTimeRange() {
		return toInServerTz.getTime() - fromInServerTz.getTime();
	}

	public boolean isWiderRangeThan(DateRange other) {
		return this.getTimeRange() > other.getTimeRange();
	}
}
