package org.openmrs.module.patientgrid;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

@Entity
@Table(name = "patientgrid_patient_grid_column")
@Inheritance(strategy = InheritanceType.JOINED)
public class PatientGridColumn extends BaseOpenmrsObject implements Auditable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "patient_grid_column_id")
	private Integer patientGridColumnId;
	
	@NotNull
	@Column(nullable = false)
	private String name;
	
	@Column
	private String description;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_grid_id", nullable = false)
	private PatientGrid patientGrid;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = PatientGridConstants.PROP_DATATYPE, nullable = false, updatable = false, length = 50)
	private ColumnDatatype datatype;
	
	@NotNull
	@Column(name = "is_hidden", nullable = false)
	private Boolean hidden = false;
	
	@Access(AccessType.FIELD)
	@OneToMany(mappedBy = "patientGridColumn", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<PatientGridColumnFilter> filters;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "creator", nullable = false, updatable = false)
	private User creator;
	
	@NotNull
	@Column(name = "date_created", nullable = false, updatable = false)
	private Date dateCreated;
	
	@ManyToOne
	@JoinColumn(name = "changed_by")
	private User changedBy;
	
	@Column(name = "date_changed")
	private Date dateChanged;
	
	public PatientGridColumn() {
	}
	
	public PatientGridColumn(String name, ColumnDatatype datatype) {
		this.name = name;
		this.datatype = datatype;
	}
	
	public enum ColumnDatatype {
		
		NAME(new Displayer.DefaultFilter()),
		GENDER(new Displayer.DefaultFilter()),
		ENC_DATE(new Displayer.DateFilter()),
		ENC_AGE(new Displayer.DefaultFilter()),
		OBS(new Displayer.DefaultFilter()),
		ENC_LOCATION(new Displayer.DefaultFilter()),
		ENC_COUNTRY(new Displayer.DefaultFilter());
		
		private final Displayer<PatientGridColumnFilter> displayer;
		
		ColumnDatatype(final Displayer<PatientGridColumnFilter> displayer) {
			this.displayer = displayer;
		}
		
		public Displayer<PatientGridColumnFilter> getDisplayer() {
			return displayer;
		}
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
	 * Gets the name
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the description
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * Gets the hidden
	 *
	 * @return the hidden
	 */
	public Boolean getHidden() {
		return hidden;
	}
	
	/**
	 * Sets the hidden
	 *
	 * @param hidden the hidden to set
	 */
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}
	
	/**
	 * Gets the filters
	 *
	 * @return the filters
	 */
	public Set<PatientGridColumnFilter> getFilters() {
		if (filters == null) {
			filters = new LinkedHashSet();
		}
		
		return filters;
	}
	
	/**
	 * Adds a filter to the list of filter definitions for this column
	 *
	 * @param filter the filter to add
	 */
	public void addFilter(PatientGridColumnFilter filter) {
		filter.setPatientGridColumn(this);
		getFilters().add(filter);
	}
	
	/**
	 * Removes a filter from the list of filters for this column
	 *
	 * @param filter the filter to remove
	 * @return true if the filter was found and removed otherwise false
	 */
	public boolean removeFilter(PatientGridColumnFilter filter) {
		if (filter != null) {
			return getFilters().remove(filter);
		}
		
		return false;
	}
	
	/**
	 * @see Auditable#getCreator()
	 */
	@Override
	public User getCreator() {
		return creator;
	}
	
	/**
	 * @see Auditable#setCreator(User)
	 */
	@Override
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	/**
	 * @see Auditable#getDateCreated()
	 */
	@Override
	public Date getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * @see Auditable#setDateCreated(Date)
	 */
	@Override
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	/**
	 * @see Auditable#getChangedBy()
	 */
	@Override
	public User getChangedBy() {
		return changedBy;
	}
	
	/**
	 * @see Auditable#setChangedBy(User)
	 */
	@Override
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}
	
	/**
	 * @see Auditable#getDateChanged()
	 */
	@Override
	public Date getDateChanged() {
		return dateChanged;
	}
	
	/**
	 * @see Auditable#setDateChanged(Date)
	 */
	@Override
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	
}
