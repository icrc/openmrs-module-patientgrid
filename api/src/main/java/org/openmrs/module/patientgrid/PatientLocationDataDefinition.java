package org.openmrs.module.patientgrid;

import org.openmrs.Location;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

public class PatientLocationDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	@Override
	public Class<?> getDataType() {
		return Location.class;
	}
	
}
