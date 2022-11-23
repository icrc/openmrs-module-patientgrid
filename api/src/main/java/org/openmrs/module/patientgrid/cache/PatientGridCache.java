package org.openmrs.module.patientgrid.cache;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.patientgrid.xstream.CustomXstreamSerializer;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

/**
 * Custom implementation of spring's {@link Cache} abstraction backed by a {@link DiskCache}
 */
public class PatientGridCache implements Cache {
	
	private static final Logger log = LoggerFactory.getLogger(PatientGridCache.class);
	
	private DiskCache diskCache;
	
	private CustomXstreamSerializer serializer;
	
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
	
	protected void setSerializer(CustomXstreamSerializer serializer) {
		this.serializer = serializer;
	}
	
	protected CustomXstreamSerializer getSerializer() {
		if (serializer == null) {
			try {
				serializer = new CustomXstreamSerializer();
			}
			catch (SerializationException e) {
				log.error("can't create CustomXstreamSerializer");
			}
		}
		return serializer;
	}
	
	/**
	 * @see Cache#get(Object, Class)
	 */
	@Override
	public <T> T get(Object key, Class<T> type) {
		File targetFile = getDiskCache().getFile(key.toString());
		if (targetFile == null || !targetFile.exists()) {
			return null;
		}
		T value = null;
		try {
			value = (T) getSerializer().fromXML(targetFile);
		}
		catch (IOException e) {
			log.warn("Failed to deserialize cached grid report", e);
		}
		
		return value;
	}
	
	/**
	 * @see Cache#put(Object, Object)
	 */
	@Override
	public void put(Object key, Object value) {
		if (value == null) {
			return;
		}
		File targetFile = getDiskCache().getFile(key.toString());
		
		try {
			getSerializer().toXML(value, targetFile);
			
		}
		catch (IOException e) {
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
		List<String> filenames = Collections.singletonList(key.toString());
		//If key is patient grid uuid ONLY then delete all reports for the grid for all users
		if (StringUtils.split(key.toString(), PatientGridConstants.CACHE_KEY_SEPARATOR).length == 1) {
			String[] files = getDiskCache().getCacheDirectory().list((dir, name) -> name.startsWith(key.toString()));
			filenames = Arrays.asList(files);
		}
		
		filenames.stream().forEach(filename -> getDiskCache().deleteFile(filename));
	}
	
	/**
	 * @see Cache#clear()
	 */
	@Override
	public void clear() {
		getDiskCache().deleteAllFiles();
	}
	
}
