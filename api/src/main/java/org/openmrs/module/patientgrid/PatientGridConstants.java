package org.openmrs.module.patientgrid;

import org.openmrs.module.patientgrid.converter.PatientGridObsConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PatientGridConstants {
	
	public static final String MODULE_ID = "patientgrid";
	
	public static final String PRIV_MANAGE_PATIENT_GRIDS = "Manage Patient Grids";
	
	public static final String COLUMN_UUID = "uuid";
	
	public static final String KEY_MOST_RECENT_ENCS = PatientGridConstants.class.getName() + "_ENCS";
	
	public static final String KEY_AGES_AT_ENCS = PatientGridConstants.class.getName() + "_AGES";
	
	public static final String GP_AGE_RANGES = MODULE_ID + ".age.ranges";
	
	public static final String GP_DISK_CACHE_DIR = MODULE_ID + ".cacheDirectory";
	
	public static final String CACHE_MANAGER_NAME = "patientGridReportsCacheManager";
	
	public static final String CACHE_NAME_GRID_REPORTS = "patientGridReports";
	
	public static final String DEFAULT_DISK_CACHE_DIR_NAME = ".report_cache";
	
	public static final String CACHE_KEY_SEPARATOR = "_";
	
	public static final String CACHE_EVICT_KEY_EXP = "#patientGrid.getUuid()";
	
	public static final String CACHE_EVICT_CONDITION_EXP = "#patientGrid.getId() != null";
	
	public static final String CACHE_KEY_EXP = "#patientGrid.getUuid()+'" + CACHE_KEY_SEPARATOR
	        + "'+T(org.openmrs.api.context.Context).getAuthenticatedUser().getUuid()";
	
	public static final String CACHE_CONDITION_EXP = "T(org.openmrs.api.context.Context).getAuthenticatedUser() != null";
	
	public static final String CACHE_UNLESS_EXP = "#result.getRows().isEmpty() || T(org.openmrs.api.context.Context).getAuthenticatedUser() == null";
	
	public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssXXX");
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final PatientGridObsConverter OBS_CONVERTER = new PatientGridObsConverter();
	
}
