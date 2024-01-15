package org.openmrs.module.patientgrid.filter;

import org.openmrs.module.patientgrid.period.DateRange;

public class ObjectWithDateRange<T extends Object> {

	private final T object;

	private final DateRange dateRange;

	public ObjectWithDateRange(final T object, final DateRange dateRange) {
		this.object = object;
		this.dateRange = dateRange;
	}

	public T getObject() {
		return object;
	}

	public DateRange getDateRange() {
		return dateRange;
	}
}
