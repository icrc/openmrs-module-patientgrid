package org.openmrs.module.patientgrid.definition;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.module.patientgrid.filter.definition.LocationCohortDefinition;
import org.openmrs.module.patientgrid.period.DateRange;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class ObsForLatestEncounterPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	@ConfigurationProperty
	private Concept concept;

	@ConfigurationProperty
	private EncounterType encounterType;

	@ConfigurationProperty
	private DateRange periodRange;

	@ConfigurationProperty
	private LocationCohortDefinition locationCohortDefinition;

	@ConfigurationProperty
	private String questionId;

	public DateRange getPeriodRange() {
		return periodRange;
	}

	public void setPeriodRange(DateRange periodRange) {
		this.periodRange = periodRange;
	}

	@Override
	public Class<?> getDataType() {
		return Obs.class;
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

	public LocationCohortDefinition getLocationCohortDefinition() {
		return locationCohortDefinition;
	}

	public void setLocationCohortDefinition(LocationCohortDefinition locationCohortDefinition) {
		this.locationCohortDefinition = locationCohortDefinition;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
}
