package org.openmrs.module.patientgrid;

import org.openmrs.module.patientgrid.converter.PatientGridObsConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PatientGridConstants {

	public static final String PROP_SHARED = "shared";

	public static final String PROP_COLUMNS = "columns";

	public static final String CONVERT_TO_AGE_RANGE = "convertToAgeRange";

	/**
	 * Utility class
	 */
	private PatientGridConstants() {

	}

	public static final String MODULE_ID = "patientgrid";

	public static final String PRIV_MANAGE_PATIENT_GRIDS = "Manage Patient Grids";

	public static final String COLUMN_UUID = "uuid";

	public static final String GP_AGE_RANGES = MODULE_ID + ".age.ranges";

	/**
	 * default period definition to use if no period given or not supported by the system
	 */
	public static final String GP_DEFAULT_PERIOD_RANGE = MODULE_ID + ".defaultPeriod";

	public static final String GP_MAX_CACHE_FILE_AGE = MODULE_ID + ".maxCacheFileAge";

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

	public static final String CACHE_UNLESS_EXP = "#result.getSimpleDataSet().getRows().isEmpty() || T(org.openmrs.api.context.Context).getAuthenticatedUser() == null";

	public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssXXX");

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static final PatientGridObsConverter OBS_CONVERTER = new PatientGridObsConverter();

	public static final String GP_ROWS_COUNT_LIMIT = MODULE_ID + ".rowsLimit";

	public static final String PROPERTY_DISPLAY = "display";

	public static final String PROPERTY_ENCOUNTER_TYPE = "encounterType";

	public static final String PROPERTY_COLUMN = "column";

	public static final String PROPERTY_OPERAND = "operand";

	public static final String PROP_CONCEPT = "concept";

	public static final String PROP_DESCRIPTION = "description";

	public static final String PROP_DATATYPE = "datatype";

	public static final String PROP_HIDDEN = "hidden";

	public static final String PROP_FILTERS = "filters";
}
