package org.openmrs.module.patientgrid;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.Concept;
import org.openmrs.EncounterType;

//@Entity
@Table(name = "obs_patient_grid_column")
public class ObsPatientGridColumn extends PatientGridColumn {
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "concept_id", nullable = false)
	private Concept concept;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "encounter_type_id", nullable = false)
	private EncounterType encounterType;
	
	public ObsPatientGridColumn() {
		super(null, null);
	}
	
	public ObsPatientGridColumn(String name, Concept concept, EncounterType encounterType) {
		super(name, ColumnDatatype.OBS);
		this.concept = concept;
		this.encounterType = encounterType;
	}
	
	/**
	 * Gets the concept
	 *
	 * @return the concept
	 */
	public Concept getConcept() {
		return concept;
	}
	
	/**
	 * Sets the concept
	 *
	 * @param concept the concept to set
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
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
