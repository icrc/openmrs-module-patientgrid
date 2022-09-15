package org.openmrs.module.patientgrid.filter;

import java.util.List;

import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Custom CohortDefinition for getting a cohort of patients associated to specific locations
 */
public class LocationCohortDefinition extends BaseCohortDefinition {
	
	@ConfigurationProperty
	private List<Location> locations;
	
	@ConfigurationProperty
	private boolean country = false;
	
	/**
	 * Gets the locations
	 *
	 * @return the locations
	 */
	public List<Location> getLocations() {
		return locations;
	}
	
	/**
	 * Sets the locations
	 *
	 * @param locations the locations to set
	 */
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	
	/**
	 * Gets the country
	 *
	 * @return the country
	 */
	public boolean getCountry() {
		return country;
	}
	
	/**
	 * Sets the country
	 *
	 * @param country the country to set
	 */
	public void setCountry(boolean country) {
		this.country = country;
	}
	
}
