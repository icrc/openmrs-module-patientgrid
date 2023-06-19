package org.openmrs.module.patientgrid;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

/**
 * Encapsulates information about a filter to be applied to a patient grid column
 */
@Entity
@Table(name = "patientgrid_patient_grid_column_filter")
public class PatientGridColumnFilter extends BaseOpenmrsObject implements Auditable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "patient_grid_column_filter_id")
	private Integer patientGridColumnFilterId;
	
	@Column(nullable = false)
	private String name;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_grid_column_id", nullable = false)
	private PatientGridColumn patientGridColumn;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private FilterOperator operator = FilterOperator.EQUALS;;
	
	@Column(nullable = false)
	private String operand;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "creator", nullable = false, updatable = false)
	private User creator;
	
	@Column(name = "date_created", nullable = false, updatable = false)
	private Date dateCreated;
	
	@ManyToOne
	@JoinColumn(name = "changed_by")
	private User changedBy;
	
	@Column(name = "date_changed")
	private Date dateChanged;
	
	public enum FilterOperator {
		EQUALS
	}
	
	public PatientGridColumnFilter() {
	}
	
	public PatientGridColumnFilter(String name, String operand) {
		this.name = name;
		this.operand = operand;
	}
	
	/**
	 * Gets the patientGridColumnFilterId
	 *
	 * @return the patientGridColumnFilterId
	 */
	public Integer getPatientGridColumnFilterId() {
		return patientGridColumnFilterId;
	}
	
	/**
	 * Sets the patientGridColumnFilterId
	 *
	 * @param patientGridColumnFilterId the patientGridColumnFilterId to set
	 */
	public void setPatientGridColumnFilterId(Integer patientGridColumnFilterId) {
		this.patientGridColumnFilterId = patientGridColumnFilterId;
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
	 * Gets the patientGridColumn
	 *
	 * @return the patientGridColumn
	 */
	public PatientGridColumn getPatientGridColumn() {
		return patientGridColumn;
	}
	
	/**
	 * Sets the patientGridColumn
	 *
	 * @param patientGridColumn the patientGridColumn to set
	 */
	public void setPatientGridColumn(PatientGridColumn patientGridColumn) {
		this.patientGridColumn = patientGridColumn;
	}
	
	/**
	 * Gets the operator
	 *
	 * @return the operator
	 */
	public FilterOperator getOperator() {
		return operator;
	}
	
	/**
	 * Sets the operator
	 *
	 * @param operator the operator to set
	 */
	public void setOperator(FilterOperator operator) {
		this.operator = operator;
	}
	
	/**
	 * Gets the operand
	 *
	 * @return the operand
	 */
	public String getOperand() {
		return operand;
	}
	
	/**
	 * Sets the operand
	 *
	 * @param operand the operand to set
	 */
	public void setOperand(String operand) {
		this.operand = operand;
	}
	
	/**
	 * @see BaseOpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getPatientGridColumnFilterId();
	}
	
	/**
	 * @see BaseOpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setPatientGridColumnFilterId(id);
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
