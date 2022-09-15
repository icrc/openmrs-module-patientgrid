package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_MANAGER_FACTORY_NAME;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class PatientGridCachingConfig {
	
	@Bean(name = CACHE_MANAGER_FACTORY_NAME)
	public EhCacheManagerFactoryBean getPatientGridCacheManagerFactoryBean() {
		//TODO Change to a cache mechanism which survives application restarts
		EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
		factory.setCacheManagerName(org.openmrs.module.patientgrid.PatientGridConstants.CACHE_MANAGER_NAME);
		factory.setConfigLocation(new ClassPathResource("ehcache-patientgrids.xml"));
		
		return factory;
	}
	
	@Bean(name = org.openmrs.module.patientgrid.PatientGridConstants.CACHE_MANAGER_NAME)
	public CacheManager cacheManager(@Qualifier(CACHE_MANAGER_FACTORY_NAME) EhCacheManagerFactoryBean factoryBean) {
		return new EhCacheCacheManager(factoryBean.getObject());
	}
	
}
