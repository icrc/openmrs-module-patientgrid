package org.openmrs.module.patientgrid;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.EncounterType;

//@Entity
@Table(name = "enc_age_patient_grid_column")
public class AgeAtEncounterPatientGridColumn extends PatientGridColumn {
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "encounter_type_id", nullable = false)
	private EncounterType encounterType;
	
	@Column(name = "convert_to_age_range", nullable = false)
	private Boolean convertToAgeRange = false;
	
	public AgeAtEncounterPatientGridColumn() {
		super(null, null);
	}
	
	public AgeAtEncounterPatientGridColumn(String name, EncounterType encounterType) {
		this(name, encounterType, false);
	}
	
	public AgeAtEncounterPatientGridColumn(String name, EncounterType encounterType, boolean convertToAgeRange) {
		super(name, ColumnDatatype.ENC_AGE);
		this.encounterType = encounterType;
		this.convertToAgeRange = convertToAgeRange;
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
	
	/**
	 * Gets the convertToAgeRange
	 *
	 * @return the convertToAgeRange
	 */
	public Boolean getConvertToAgeRange() {
		return convertToAgeRange;
	}
	
	/**
	 * Sets the convertToAgeRange
	 *
	 * @param convertToAgeRange the convertToAgeRange to set
	 */
	public void setConvertToAgeRange(Boolean convertToAgeRange) {
		this.convertToAgeRange = convertToAgeRange;
	}
	
}
