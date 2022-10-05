package org.openmrs.module.patientgrid.definition;

import java.util.Date;

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class DateForLatestEncounterDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	@Override
	public Class<?> getDataType() {
		return Date.class;
	}
	
	/**
	 * Gets the encounterType
	 *
	 * @return the encounterType
	 */
	public EncounterType getEncounterType() {
		return encounterType;
	}
	
	/**
	 * Sets the encounterType
	 *
	 * @param encounterType the encounterType to set
	 */
	public void setEncounterType(EncounterType encounterType) {
		this.encounterType = encounterType;
	}
	
}
