package org.openmrs.module.patientgrid.filter.definition;

import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Custom CohortDefinition for getting a cohort of patients matching specific age ranges at their
 * respective most recent encounters of a specific type.
 */
public class AgeRangeAtLatestEncounterCohortDefinition extends BaseCohortDefinition {
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	@ConfigurationProperty
	private List<AgeRange> ageRanges;
	
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
	 * Gets the ageRanges
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
	
}
