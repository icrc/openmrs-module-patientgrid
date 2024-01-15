package org.openmrs.module.patientgrid.cache;

import static org.openmrs.module.patientgrid.PatientGridConstants.CACHE_NAME_GRID_REPORTS;

import java.util.Collection;
import java.util.Collections;

import org.openmrs.api.APIException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class PatientGridCacheManager implements CacheManager {

	private PatientGridCache cache;

	@Override
	public Cache getCache(String name) {
		if (!getCacheNames().contains(name)) {
			throw new APIException("No cache found with name: " + name);
		}

		if (cache == null) {
			cache = new PatientGridCache();
		}

		return cache;
	}

	@Override
	public Collection<String> getCacheNames() {
		return Collections.singleton(CACHE_NAME_GRID_REPORTS);
	}

}
