package org.openmrs.module.patientgrid.cache;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.period.DateRangeConverter;
import org.openmrs.module.patientgrid.xstream.CustomXstreamSerializer;
import org.openmrs.serialization.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * Custom implementation of spring's {@link Cache} abstraction backed by a {@link DiskCache}
 */
public class PatientGridCache implements Cache {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientGridCache.class);
	
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
		ExtendedDataSet dataset = get(key, ExtendedDataSet.class);
		if (dataset != null && !isObsolete(dataset)) {
			ret = new SimpleValueWrapper(dataset);
		}
		
		return ret;
	}
	
	boolean isObsolete(ExtendedDataSet dataSet) {
		if (!dataSet.isLastVersion()) {
			LOGGER.debug("the xml version is not the last one. Force recompute. Read Version: {}. Current Version {}",
			    dataSet.getXstreamVersion(), ExtendedDataSet.LAST_XSTREAM_VERSION);
			return true;
		}
		String periodOperand = dataSet.getPeriodOperand();
		if (periodOperand != null) {
			String usedDateRange = dataSet.getUsedDateRange();
			DateRangeConverter converter = new DateRangeConverter(PatientGridUtils.getCurrentUserTimeZone());
			String dateRangeAsString = converter.convert(periodOperand, DateTime.now()).getDateRangeAsString();
			if (!usedDateRange.equals(dateRangeAsString)) {
				LOGGER.debug("the dateRange used to compute the grid changed. Force recompute. Old: {}. New {}",
				    usedDateRange, dateRangeAsString);
				return true;
			}
			
		}
		return false;
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
				LOGGER.error("can't create CustomXstreamSerializer");
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
		//to show that the file has been used and should not be clean by cleaner task.
		boolean lastModified = targetFile.setLastModified(System.currentTimeMillis());
		if (!lastModified) {
			LOGGER.warn("unable to update last modified property for file {}", targetFile);
		}
		T value = null;
		try {
			value = (T) getSerializer().fromXML(targetFile);
		}
		catch (IOException e) {
			LOGGER.warn("Failed to deserialize cached grid report", e);
			return null;
		}
		if (value != null && !value.getClass().equals(type)) {
			return null;
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
			LOGGER.warn("Failed to serialize grid report", e);
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
