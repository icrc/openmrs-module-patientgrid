package org.openmrs.module.patientgrid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.openmrs.EncounterType;

@Entity
@Table(name = "patientgrid_enc_age_patient_grid_column")
public class AgeAtEncounterPatientGridColumn extends BaseEncounterTypePatientGridColumn {
	
	@Column(name = "convert_to_age_range", nullable = false)
	private Boolean convertToAgeRange;
	
	public AgeAtEncounterPatientGridColumn() {
		this(null, null);
	}
	
	public AgeAtEncounterPatientGridColumn(String name, EncounterType encounterType) {
		this(name, encounterType, false);
	}
	
	public AgeAtEncounterPatientGridColumn(String name, EncounterType encounterType, boolean convertToAgeRange) {
		super(name, ColumnDatatype.ENC_AGE, encounterType);
		this.convertToAgeRange = convertToAgeRange;
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
