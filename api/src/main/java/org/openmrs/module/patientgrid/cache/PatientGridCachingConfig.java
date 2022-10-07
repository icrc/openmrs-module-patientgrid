package org.openmrs.module.patientgrid.cache;

import org.openmrs.module.patientgrid.PatientGridConstants;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatientGridCachingConfig {
	
	@Bean(name = PatientGridConstants.CACHE_MANAGER_NAME)
	public CacheManager cacheManager() {
		return new PatientGridCacheManager();
	}
	
}
