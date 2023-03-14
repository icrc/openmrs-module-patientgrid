package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype.OBS;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.*;

@Entity
@Table(name = "patientgrid_patient_grid")
public class PatientGrid extends BaseChangeableOpenmrsMetadata {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "patient_grid_id")
	private Integer patientGridId;
	
	@Access(AccessType.FIELD)
	@OneToMany(mappedBy = "patientGrid", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<PatientGridColumn> columns;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User owner;
	
	@NotNull
	@Column(name = "is_shared", nullable = false)
	private Boolean shared = false;
	
	@ManyToOne
	@JoinColumn(name = "cohort_id")
	private Cohort cohort;
	
	/**
	 * @see BaseOpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getPatientGridId();
	}
	
	/**
	 * @see BaseOpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setPatientGridId(id);
	}
	
	/**
	 * Gets the patientGridId
	 *
	 * @return the patientGridId
	 */
	public Integer getPatientGridId() {
		return patientGridId;
	}
	
	/**
	 * Sets the patientGridId
	 *
	 * @param patientGridId the patientGridId to set
	 */
	public void setPatientGridId(Integer patientGridId) {
		this.patientGridId = patientGridId;
	}
	
	/**
	 * Gets the columns
	 *
	 * @return the columns
	 */
	public Set<PatientGridColumn> getColumns() {
		if (columns == null) {
			columns = new LinkedHashSet();
		}
		
		return columns;
	}
	
	/**
	 * Adds a column to the list of columns for this grid
	 *
	 * @param column the column to add
	 */
	public void addColumn(PatientGridColumn column) {
		column.setPatientGrid(this);
		getColumns().add(column);
	}
	
	/**
	 * Removes a column from the list of columns for this grid
	 *
	 * @param column the column to remove
	 * @return true if the column was found and removed otherwise false
	 */
	public boolean removeColumn(PatientGridColumn column) {
		if (column != null) {
			return getColumns().remove(column);
		}
		
		return false;
	}
	
	/**
	 * Gets the owner
	 *
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
	}
	
	/**
	 * Sets the owner
	 *
	 * @param owner the owner to set
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	/**
	 * Gets the shared
	 *
	 * @return the shared
	 */
	public Boolean getShared() {
		return shared;
	}
	
	/**
	 * Sets the shared
	 *
	 * @param shared the shared to set
	 */
	public void setShared(Boolean shared) {
		this.shared = shared;
	}
	
	/**
	 * Gets the cohort
	 *
	 * @return the cohort
	 */
	public Cohort getCohort() {
		return cohort;
	}
	
	/**
	 * Sets the cohort
	 *
	 * @param cohort the cohort to set
	 */
	public void setCohort(Cohort cohort) {
		this.cohort = cohort;
	}
	
	/**
	 * Gets all obs columns in this grid
	 *
	 * @return set of {@link ObsPatientGridColumn} objects
	 */
	public Set<ObsPatientGridColumn> getObsColumns() {
		return getColumns().stream().filter(c -> c.getDatatype() == OBS).map(c -> (ObsPatientGridColumn) c)
		        .collect(Collectors.toSet());
	}
	
	public EncounterDatePatientGridColumn getDateColumn(EncounterType type) {
		return getColumns().stream()
		        .filter(c -> c.getDatatype() == PatientGridColumn.ColumnDatatype.ENC_DATE
		                && type.equals(((EncounterDatePatientGridColumn) c).getEncounterType()))
		        .map(c -> (EncounterDatePatientGridColumn) c).findFirst().orElse(null);
	}
	
}
