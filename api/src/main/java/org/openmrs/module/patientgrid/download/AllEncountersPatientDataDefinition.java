package org.openmrs.module.patientgrid.download;

import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class AllEncountersPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	@ConfigurationProperty
	private PatientGrid patientGrid;
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	@Override
	public Class<?> getDataType() {
		return List.class;
	}
	
	/**
	 * Gets the patientGrid
	 *
	 * @return the patientGrid
	 */
	public PatientGrid getPatientGrid() {
		return patientGrid;
	}
	
	/**
	 * Sets the patientGrid
	 *
	 * @param patientGrid the patientGrid to set
	 */
	public void setPatientGrid(PatientGrid patientGrid) {
		this.patientGrid = patientGrid;
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
