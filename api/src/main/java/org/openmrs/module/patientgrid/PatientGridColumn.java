package org.openmrs.module.patientgrid;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.BaseChangeableOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;

//@Entity
@Table(name = "patientgrid_patient_grid_column")
@Inheritance(strategy = InheritanceType.JOINED)
public class PatientGridColumn extends BaseChangeableOpenmrsMetadata {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "patient_grid_column_id")
	private Integer patientGridColumnId;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_grid_id", nullable = false)
	private org.openmrs.module.patientgrid.PatientGrid patientGrid;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "datatype", nullable = false, length = 50)
	private ColumnDatatype datatype;
	
	//@Transient
	//private List<FilterDef> filters;
	
	public PatientGridColumn() {
	}
	
	public PatientGridColumn(String name, ColumnDatatype datatype) {
		setName(name);
		this.datatype = datatype;
	}
	
	public enum ColumnDatatype {
		NAME,
		GENDER,
		ENC_AGE,
		OBS,
		DATAFILTER_LOCATION,
		DATAFILTER_COUNTRY
	}
	
	/**
	 * @see BaseOpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getPatientGridColumnId();
	}
	
	/**
	 * @see BaseOpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setPatientGridColumnId(id);
	}
	
	/**
	 * Gets the patientGridColumnId
	 *
	 * @return the patientGridColumnId
	 */
	public Integer getPatientGridColumnId() {
		return patientGridColumnId;
	}
	
	/**
	 * Sets the patientGridColumnId
	 *
	 * @param patientGridColumnId the patientGridColumnId to set
	 */
	public void setPatientGridColumnId(Integer patientGridColumnId) {
		this.patientGridColumnId = patientGridColumnId;
	}
	
	/**
	 * Gets the patientGrid
	 *
	 * @return the patientGrid
	 */
	public org.openmrs.module.patientgrid.PatientGrid getPatientGrid() {
		return patientGrid;
	}
	
	/**
	 * Sets the patientGrid
	 *
	 * @param patientGrid the patientGrid to set
	 */
	public void setPatientGrid(org.openmrs.module.patientgrid.PatientGrid patientGrid) {
		this.patientGrid = patientGrid;
	}
	
	/**
	 * Gets the datatype
	 *
	 * @return the datatype
	 */
	public ColumnDatatype getDatatype() {
		return datatype;
	}
	
	/**
	 * Sets the datatype
	 *
	 * @param datatype the datatype to set
	 */
	public void setDatatype(ColumnDatatype datatype) {
		this.datatype = datatype;
	}
	
	/**
	 * Gets the filters
	 *
	 * @return the filters
	 * @Transient public List<FilterDef> getFilters() { if (filters == null) { filters = new
	 *            ArrayList(); } return filters; }
	 */
	
	/**
	 * Adds a filter to the list of filter definitions for this column
	 *
	 * @param filter the filter to add
	 */
	public void addFilter(org.openmrs.module.patientgrid.FilterDef filter) {
		//getFilters().add(filter);
	}
	
}
