package org.openmrs.module.patientgrid;

import java.io.Serializable;

import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * Custom implementation of spring's {@link Cache} abstraction backed by a {@link DiskCache}
 */
public class PatientGridCache implements Cache {
	
	private DiskCache diskCache;
	
	private DiskCache getDiskCache() {
		if (diskCache == null) {
			diskCache = DiskCache.getInstance();
		}
		
		return diskCache;
	}
	
	/**
	 * @see Cache#getName()
	 */
	@Override
	public String getName() {
		return PatientGridConstants.CACHE_NAME_GRID_REPORTS;
	}
	
	/**
	 * @see Cache#getNativeCache()
	 */
	@Override
	public Object getNativeCache() {
		return getDiskCache();
	}
	
	/**
	 * @see Cache#get(Object)
	 */
	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper ret = null;
		SimpleDataSet dataset = get(key, SimpleDataSet.class);
		if (dataset != null) {
			ret = new SimpleValueWrapper(dataset);
		}
		
		return ret;
	}
	
	/**
	 * @see Cache#get(Object, Class)
	 */
	@Override
	public <T> T get(Object key, Class<T> type) {
		return getDiskCache().getFileContents(key.toString());
	}
	
	/**
	 * @see Cache#put(Object, Object)
	 */
	@Override
	public void put(Object key, Object value) {
		getDiskCache().setFileContents(key.toString(), (Serializable) value);
	}
	
	/**
	 * @see Cache#putIfAbsent(Object, Object)
	 */
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		ValueWrapper existingValue = get(key);
		if (existingValue == null) {
			put(key, value);
			return null;
		}
		
		return existingValue;
	}
	
	/**
	 * @see Cache#evict(Object)
	 */
	@Override
	public void evict(Object key) {
		//TODO if key is patient grid uuid ONLY then delete all files starting with it
		getDiskCache().deleteFile(key.toString());
	}
	
	/**
	 * @see Cache#clear()
	 */
	@Override
	public void clear() {
		getDiskCache().deleteAllFiles();
	}
	
}
