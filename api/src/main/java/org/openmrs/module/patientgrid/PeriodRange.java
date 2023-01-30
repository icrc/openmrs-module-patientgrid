package org.openmrs.module.patientgrid;

import java.util.Date;

public class PeriodRange {
	
	private Date fromDate;
	
	private Date toDate;
	
	public PeriodRange(Date fromDate, Date toDate) {
		this.fromDate = fromDate;
		this.toDate = toDate;
	}
	
	public Date getToDate() {
		return toDate;
	}
	
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
}
