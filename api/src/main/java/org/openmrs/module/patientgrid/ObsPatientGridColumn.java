package org.openmrs.module.patientgrid;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.Concept;
import org.openmrs.EncounterType;

@Entity
@Table(name = "patientgrid_obs_patient_grid_column")
public class ObsPatientGridColumn extends BaseEncounterTypePatientGridColumn {

	@ManyToOne(optional = false)
	@JoinColumn(name = "concept_id", nullable = false)
	private Concept concept;

	public ObsPatientGridColumn() {
		this(null, null, null);
	}

	public ObsPatientGridColumn(String name, Concept concept, EncounterType encounterType) {
		super(name, ColumnDatatype.OBS, encounterType);
		this.concept = concept;
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

}
