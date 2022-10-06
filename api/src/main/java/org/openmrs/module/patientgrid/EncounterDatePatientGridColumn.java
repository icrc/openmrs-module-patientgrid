package org.openmrs.module.patientgrid;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.openmrs.EncounterType;

@Entity
@Table(name = "patientgrid_enc_date_patient_grid_column")
public class EncounterDatePatientGridColumn extends BaseEncounterTypePatientGridColumn {
	
	public EncounterDatePatientGridColumn() {
		this(null, null);
	}
	
	public EncounterDatePatientGridColumn(String name, EncounterType encounterType) {
		super(name, ColumnDatatype.ENC_DATE, encounterType);
	}
	
}
