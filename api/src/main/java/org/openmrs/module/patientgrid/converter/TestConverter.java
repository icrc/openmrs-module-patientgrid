package org.openmrs.module.patientgrid.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

public class TestConverter implements DataConverter {
	
	@Override
	public Object convert(Object original) {
		return original;
	}
	
	@Override
	public Class<?> getInputDataType() {
		return Object.class;
	}
	
	@Override
	public Class<?> getDataType() {
		return Object.class;
	}
}
