package org.openmrs.module.patientgrid.filter;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.BaseEncounterTypePatientGridColumn;

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
