package org.openmrs.module.patientgrid.cache;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openmrs.module.patientgrid.PatientGridConstants.GP_MAX_CACHE_FILE_AGE;

public class CleanCacheTask extends AbstractTask {
	
	private final Logger log = LoggerFactory.getLogger(CleanCacheTask.class);
	
	@Override
	public void execute() {
		if (!isExecuting) {
			log.debug("Starting cleaning patient Grid disk cache...");
			
			startExecuting();
			try {
				int defaultValue = 120;
				String maxAgeDefinedInGP = Context.getAdministrationService().getGlobalProperty(GP_MAX_CACHE_FILE_AGE, null);
				int maxAge = defaultValue;
				if (StringUtils.isNotBlank(maxAgeDefinedInGP)) {
					try {
						maxAge = Integer.parseInt(maxAgeDefinedInGP);
					}
					catch (NumberFormatException e) {
						log.warn("The maxAge '{}' defined in the global property '{}' is not supported", maxAgeDefinedInGP,
						    GP_MAX_CACHE_FILE_AGE);
						throw new RuntimeException(e);
					}
					
				}
				DiskCache.getInstance().deleteCacheFileOlderThan(maxAge);
			}
			catch (Exception e) {
				log.error("Error while cleaning disk cache:", e);
			}
			finally {
				stopExecuting();
			}
		}
	}
	
	@Override
	public void shutdown() {
		log.debug("stop cleaning disk cache");
		this.stopExecuting();
	}
}
