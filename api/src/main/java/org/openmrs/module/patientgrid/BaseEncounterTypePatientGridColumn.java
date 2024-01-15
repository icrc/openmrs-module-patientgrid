package org.openmrs.module.patientgrid;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openmrs.EncounterType;

/**
 * Base class for PatientGridColumns that are associated to an {@link EncounterType}
 */
@MappedSuperclass
public abstract class BaseEncounterTypePatientGridColumn extends PatientGridColumn {

	@ManyToOne(optional = false)
	@JoinColumn(name = "encounter_type_id", nullable = false)
	private EncounterType encounterType;

	protected BaseEncounterTypePatientGridColumn(String name, ColumnDatatype datatype, EncounterType encounterType) {
		super(name, datatype);
		this.encounterType = encounterType;
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
