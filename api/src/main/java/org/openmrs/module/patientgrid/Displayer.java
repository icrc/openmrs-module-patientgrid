package org.openmrs.module.patientgrid;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.period.DateRangeConverter;

import java.util.Locale;

public abstract class Displayer<T extends Object> {
	
	public abstract String getDisplayString(T in, Locale locale);
	
	public static class DateFilter extends Displayer<PatientGridColumnFilter> {
		
		@Override
		public String getDisplayString(PatientGridColumnFilter in, Locale locale) {
			String operand = in.getOperand();
			return DateRangeConverter.getDisplay(operand, Context.getLocale());
		}
	}
	
	public static class DefaultFilter extends Displayer<PatientGridColumnFilter> {
		
		@Override
		public String getDisplayString(PatientGridColumnFilter in, Locale locale) {
			return in.getName();
		}
	}
	
}
