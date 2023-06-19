package org.openmrs.module.patientgrid.filter.definition;

import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.period.DateRange;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.List;

/**
 * Custom CohortDefinition for getting a cohort of patients matching specific age ranges at their
 * respective most recent encounters of a specific type.
 */
public class AgeRangeAtLatestEncounterCohortDefinition extends BaseCohortDefinition {
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	@ConfigurationProperty
	private List<AgeRange> ageRanges;
	
	@ConfigurationProperty
	private DateRange periodRange;
	
	@ConfigurationProperty
	private LocationCohortDefinition locationCohortDefinition;
	
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
	
	/**
	 * @return true if all ages will be accepted
	 */
	public boolean isAllAgesAccepted() {
		return ageRanges == null || ageRanges.isEmpty();
	}
	
	/**
	 * Gets the ageRanges. If no age ranges, it means that all ages will be accepted
	 *
	 * @return the ageRanges
	 */
	public List<AgeRange> getAgeRanges() {
		return ageRanges;
	}
	
	/**
	 * Sets the ageRanges
	 *
	 * @param ageRanges the ageRanges to set
	 */
	public void setAgeRanges(List<AgeRange> ageRanges) {
		this.ageRanges = ageRanges;
	}
	
	public DateRange getPeriodRange() {
		return periodRange;
	}
	
	public void setPeriodRange(DateRange periodRange) {
		this.periodRange = periodRange;
	}
	
	public LocationCohortDefinition getLocationCohortDefinition() {
		return locationCohortDefinition;
	}
	
	public void setLocationCohortDefinition(LocationCohortDefinition locationCohortDefinition) {
		this.locationCohortDefinition = locationCohortDefinition;
	}
}
