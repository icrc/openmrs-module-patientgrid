package org.openmrs.module.patientgrid;

import org.openmrs.module.patientgrid.period.DateRange;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

/**
 * A wrapped class for {@link SimpleDataSet} giving more info on the contact: data truncated, the
 * rows count limit, used period filter...
 */
public class ExtendedDataSet {

	/**
	 * Should be incremented if there is an incompatible modification is done and older
	 * {@link ExtendedDataSet} can't be restored from cached file.
	 */
	public static final String LAST_XSTREAM_VERSION = "1.0";

	/**
	 * the wrapped DataSet
	 */
	private SimpleDataSet simpleDataSet;

	/**
	 * true if the Cohort has been truncated. Should be true if initialRowsCount> rowsCountLimit.
	 */
	private boolean truncated;

	/**
	 * The row count limit applied to this dataset
	 */
	private int rowsCountLimit;

	/**
	 * Initial number of row ( before truncation)
	 */
	private int initialRowsCount;

	/**
	 * start and date used to filter the cohort
	 */
	private String usedDateRange;

	/**
	 * the Definition of the period
	 */
	private String periodOperand;

	/**
	 * the xstream version
	 */
	private String xstreamVersion = LAST_XSTREAM_VERSION;

	public ExtendedDataSet() {
	}

	public ExtendedDataSet(final SimpleDataSet simpleDataSet, DateRange dataRange) {
		this.simpleDataSet = simpleDataSet;
		if (dataRange != null) {
			setUsedDateRange(dataRange.getDateRangeAsString());
			setPeriodOperand(dataRange.getOperand());
		}
	}

	public void setXstreamVersion(final String xstreamVersion) {
		this.xstreamVersion = xstreamVersion;
	}

	public boolean isLastVersion() {
		return LAST_XSTREAM_VERSION.equals(xstreamVersion);
	}

	public SimpleDataSet getSimpleDataSet() {
		return simpleDataSet;
	}

	public void setSimpleDataSet(SimpleDataSet simpleDataSet) {
		this.simpleDataSet = simpleDataSet;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	public int getRowsCountLimit() {
		return rowsCountLimit;
	}

	public void setRowsCountLimit(int rowsCountLimit) {
		this.rowsCountLimit = rowsCountLimit;
	}

	public int getInitialRowsCount() {
		return initialRowsCount;
	}

	public void setInitialRowsCount(int initialRowsCount) {
		this.initialRowsCount = initialRowsCount;
	}

	public String getUsedDateRange() {
		return usedDateRange;
	}

	public void setUsedDateRange(String usedDateRange) {
		this.usedDateRange = usedDateRange;
	}

	public String getPeriodOperand() {
		return periodOperand;
	}

	public void setPeriodOperand(String periodOperand) {
		this.periodOperand = periodOperand;
	}

	public String getXstreamVersion() {
		return xstreamVersion;
	}
}
