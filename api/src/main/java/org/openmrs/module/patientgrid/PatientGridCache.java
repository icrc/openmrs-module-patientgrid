package org.openmrs.module.patientgrid;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.serialization.SerializationException;
import org.openmrs.serialization.SimpleXStreamSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * Custom implementation of spring's {@link Cache} abstraction backed by a {@link DiskCache}
 */
public class PatientGridCache implements Cache {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridCache.class);
	
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
		String cachedValue = getDiskCache().getFileContents(key.toString());
		if (cachedValue == null) {
			return null;
		}
		
		T value = null;
		try {
			value = Context.getSerializationService().deserialize(cachedValue, type, SimpleXStreamSerializer.class);
		}
		catch (SerializationException e) {
			log.warn("Failed to deserialize cached grid report", e);
		}
		
		return value;
	}
	
	/**
	 * @see Cache#put(Object, Object)
	 */
	@Override
	public void put(Object key, Object value) {
		try {
			String toCache = Context.getSerializationService().serialize(value, SimpleXStreamSerializer.class);
			getDiskCache().setFileContents(key.toString(), toCache);
		}
		catch (SerializationException e) {
			log.warn("Failed to serialize grid report", e);
		}
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
