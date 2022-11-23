package org.openmrs.module.patientgrid.cache;

import static org.openmrs.module.patientgrid.PatientGridConstants.DEFAULT_DISK_CACHE_DIR_NAME;
import static org.openmrs.module.patientgrid.PatientGridConstants.GP_DISK_CACHE_DIR;
import static org.openmrs.module.patientgrid.PatientGridConstants.MODULE_ID;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
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
public class DiskCache {
	
	private static final Logger log = LoggerFactory.getLogger(DiskCache.class);
	
	private File cacheDirectory;
	
	private static class DiskCacheHolder {
		
		private final static DiskCache INSTANCE = new DiskCache();
		
	}
	
	public static DiskCache getInstance() {
		return DiskCacheHolder.INSTANCE;
	}
	
	protected File getCacheDirectory() {
		if (cacheDirectory == null) {
			log.info("Initializing disk cache");
			
			String dir = Context.getAdministrationService().getGlobalProperty(GP_DISK_CACHE_DIR);
			if (StringUtils.isBlank(dir)) {
				File parent = OpenmrsUtil.getDirectoryInApplicationDataDirectory(MODULE_ID);
				cacheDirectory = new File(parent, DEFAULT_DISK_CACHE_DIR_NAME);
			} else {
				cacheDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(dir);
			}
			
			if (!cacheDirectory.exists()) {
				log.info("Creating grid report cache directory at {}", cacheDirectory);
				
				if (!cacheDirectory.mkdirs()) {
					throw new APIException("Failed to create grid report cache directory at " + cacheDirectory);
				}
			}
		}
		
		return cacheDirectory;
	}
	
	private boolean hasFile(String filename) {
		File file = new File(getCacheDirectory(), filename);
		return file.exists() && file.isFile();
	}
	
	public File getFile(String filename) {
		return new File(getCacheDirectory(), filename);
	}
	
	public void deleteFile(String filename) {
		if (!hasFile(filename)) {
			return;
		}
		
		try {
			FileUtils.forceDelete(new File(getCacheDirectory(), filename));
		}
		catch (IOException e) {
			throw new APIException("Failed to delete file", e);
		}
	}
	
	public void deleteAllFiles() {
		try {
			FileUtils.deleteDirectory(getCacheDirectory());
		}
		catch (IOException e) {
			throw new APIException("Failed to delete directory for the disk cache", e);
		}
	}
	
}
