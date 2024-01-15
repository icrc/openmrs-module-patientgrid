package org.openmrs.module.patientgrid.web.rest;

import org.openmrs.module.patientgrid.ExtendedDataSet;

public class ReportMetadata {

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
   * the Definition of the period
   */
  private String periodOperand;

  public ReportMetadata(ExtendedDataSet extendedDataSet) {
    if (extendedDataSet != null) {
      truncated = extendedDataSet.isTruncated();
      rowsCountLimit = extendedDataSet.getRowsCountLimit();
      initialRowsCount = extendedDataSet.getInitialRowsCount();
      periodOperand = extendedDataSet.getPeriodOperand();
    }
  }

  /**
   * @return initial rows count before any truncated operation
   */
  public int getInitialRowsCount() {
    return initialRowsCount;
  }

  /**
   * @return the rows count limit applied to this result
   */
  public int getRowsCountLimit() {
    return rowsCountLimit;
  }

  /**
   * @return true if the cohort has been truncated ( if initialRowsCount > rowsCountLimit)
   */
  public boolean isTruncated() {
    return truncated;
  }

  /**
   * @return the operand used to limit the result in time (period)
   */
  public String getPeriodOperand() {
    return periodOperand;
  }

}
