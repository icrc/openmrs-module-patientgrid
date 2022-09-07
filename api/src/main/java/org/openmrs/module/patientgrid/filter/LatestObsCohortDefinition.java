package org.openmrs.module.patientgrid.filter;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Base class for custom CohortDefinitions
 */
public class LatestObsCohortDefinition extends BaseCohortDefinition {
	
	@ConfigurationProperty
	private String propertyName;
	
	@ConfigurationProperty
	private Concept concept;
	
	@ConfigurationProperty
	private EncounterType encounterType;

    @ConfigurationProperty
    private List<Object> values;
	
	/**
	 * Gets the propertyName
	 *
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}
	
	/**
	 * Sets the propertyName
	 *
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
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
	
	/**
	 * Gets the values
	 *
	 * @return the values
	 */
	public List<Object> getValues() {
		return values;
	}
	
	/**
	 * Sets the values
	 *
	 * @param values the values to set
	 */
	public void setValues(List<Object> values) {
		this.values = values;
	}
	
}
