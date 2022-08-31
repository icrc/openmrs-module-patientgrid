package org.openmrs.module.patientgrid;

import static org.openmrs.module.patientgrid.PatientGridConstants.DEFAULT_DISK_CACHE_DIR_NAME;
import static org.openmrs.module.patientgrid.PatientGridConstants.GP_DISK_CACHE_DIR;
import static org.openmrs.module.patientgrid.PatientGridConstants.MODULE_ID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom cache implementation that uses a directory to store cache entries in files, each entry in
 * the cache is saved to a file where the entry key is the filename and the entry value are the file
 * contents.
 */
public final class DiskCache {
	
	private static final Logger log = LoggerFactory.getLogger(DiskCache.class);
	
	private File cacheDirectory;
	
	private DiskCache() {
		log.info("Initializing disk cache");
		
		String dir = Context.getAdministrationService().getGlobalProperty(GP_DISK_CACHE_DIR);
		if (StringUtils.isBlank(dir)) {
			File parent = OpenmrsUtil.getDirectoryInApplicationDataDirectory(MODULE_ID);
			this.cacheDirectory = new File(parent, DEFAULT_DISK_CACHE_DIR_NAME);
		} else {
			this.cacheDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(dir);
		}
	}
	
	private static class DiskCacheHolder {
		
		private static DiskCache INSTANCE = new DiskCache();
		
	}
	
	public static DiskCache getInstance() {
		return DiskCacheHolder.INSTANCE;
	}
	
	public boolean hasFile(String filename) {
		File file = new File(cacheDirectory, filename);
		return file.exists() && file.isFile();
	}
	
	public <T> T getFileContents(String filename) {
		if (!hasFile(filename)) {
			return null;
		}
		
		try (FileInputStream in = new FileInputStream(new File(cacheDirectory, filename))) {
			return SerializationUtils.deserialize(in);
		}
		catch (IOException e) {
			throw new APIException("Failed to read from file", e);
		}
	}
	
	public void setFileContents(String filename, Serializable contents) {
		try (FileOutputStream out = new FileOutputStream(new File(cacheDirectory, filename))) {
			SerializationUtils.serialize(contents, out);
		}
		catch (IOException e) {
			throw new APIException("Failed to write to file", e);
		}
	}
	
	public void deleteFile(String filename) {
		if (!hasFile(filename)) {
			return;
		}
		
		try {
			FileUtils.forceDelete(new File(cacheDirectory, filename));
		}
		catch (IOException e) {
			throw new APIException("Failed to delete file", e);
		}
	}
	
	public void deleteAllFiles() {
		try {
			FileUtils.deleteDirectory(cacheDirectory);
		}
		catch (IOException e) {
			throw new APIException("Failed to delete directory for the disk cache", e);
		}
	}
	
}
