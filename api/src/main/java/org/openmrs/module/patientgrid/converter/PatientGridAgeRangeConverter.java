package org.openmrs.module.patientgrid.converter;

import org.openmrs.module.reporting.data.converter.AgeRangeConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * {@link AgeRangeConverter} subclass that checks for nulls before delegating back to its parent
 * converter
 */
public class PatientGridAgeRangeConverter extends AgeRangeConverter {
	
	/**
	 * @see DataConverter#convert(Object)
	 */
	@Override
	public Object convert(Object original) {
		if (original != null) {
			return super.convert(original);
		}
		
		return null;
	}
	
}
