package org.openmrs.module.patientgrid.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

public class PersonUuidDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	public PersonUuidDataDefinition() {
		super();
	}
	
	public PersonUuidDataDefinition(String name) {
		super(name);
	}
	
	public Class<?> getDataType() {
		return String.class;
	}
}
