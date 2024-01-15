package org.openmrs.module.patientgrid;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.period.DateRangeConverter;

import java.util.Locale;

public interface Displayer<T extends Object> {

  public String getDisplayString(T in, Locale locale);

  class DateFilter implements Displayer<PatientGridColumnFilter> {

    @Override
    public String getDisplayString(PatientGridColumnFilter in, Locale locale) {
      String operand = in.getOperand();
      return DateRangeConverter.getDisplay(operand, Context.getLocale());
    }
  }

  class DefaultFilter implements Displayer<PatientGridColumnFilter> {

    @Override
    public String getDisplayString(PatientGridColumnFilter in, Locale locale) {
      return in.getName();
    }
  }

}
