package org.openmrs.module.patientgrid.period;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.springframework.context.NoSuchMessageException;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateRangeConverter {
	
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().append(ISODateTimeFormat.date())
	        .appendLiteral(' ').append(ISODateTimeFormat.hourMinuteSecond()).toFormatter();
	
	private final DateTimeZone userTimeZone;
	
	public DateRangeConverter(String userTimeZone) {
		
		this.userTimeZone = DateTimeZone
		        .forTimeZone(TimeZone.getTimeZone(userTimeZone == null ? TimeZone.getDefault().getID() : userTimeZone));
	}
	
	/**
	 * @param completeDate
	 * @return
	 */
	public static String extractDateOnly(String completeDate) {
		return StringUtils.substringBefore(completeDate, " ");
	}
	
	public static String getDisplay(String operand, Locale locale) {
		Map map;
		try {
			map = PatientGridUtils.MAPPER.readValue(operand, Map.class);
		}
		catch (IOException e) {
			return operand;
		}
		String type = (String) map.get("code");
		String fromDate = extractDateOnly((String) map.get("fromDate"));
		String toDate = extractDateOnly((String) map.get("toDate"));
		try {
			String message = null;
			if (type == null && StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {
				type = DateRangeType.CUSTOMDAYSINCLUSIVE.name();
			}
			if (type != null) {
				message = Context.getMessageSourceService().getMessage("period." + type.toUpperCase(),
				    new Object[] { fromDate, toDate }, locale);
			}
			
			return StringUtils.defaultIfEmpty(message, operand);
		}
		catch (NoSuchMessageException e) {
			return operand;
		}
		
	}
	
	public DateTimeZone getUserTimeZone() {
		return this.userTimeZone;
	}
	
	private DateTime getDateForUser(Map map, String key) throws APIException {
		String asString = (String) map.get(key);
		if (StringUtils.isBlank(asString)) {
			return null;
		}
		try {
			DateTime dateTime = DateTime.parse(asString, FORMATTER);
			return dateTime.withZoneRetainFields(userTimeZone);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	private DateRangeType getRangeType(String type) {
		if (StringUtils.isNotBlank(type)) {
			try {
				return DateRangeType.valueOf(type.toUpperCase());
			}
			catch (IllegalArgumentException e) {
				return null;
			}
		}
		return DateRangeType.CUSTOMDAYSINCLUSIVE;
	}
	
	public DateRange convert(String in) throws APIException {
		return convert(in, DateTime.now());
	}
	
	public DateRange convert(String in, DateTime currentDateInServerTz) throws APIException {
		Map map;
		try {
			map = PatientGridUtils.MAPPER.readValue(in, Map.class);
		}
		catch (IOException e) {
			throw new APIException("Can't parse TimeRange: " + in, e);
		}
		final String type = (String) map.get("code");
		final DateTime fromDate = getDateForUser(map, "fromDate");
		final DateTime toDate = getDateForUser(map, "toDate");
		DateRangeParameter parameter = new DateRangeParameter(in, fromDate, toDate,
		        currentDateInServerTz == null ? null : currentDateInServerTz.withZone(getUserTimeZone()));
		DateRangeType rangeType = getRangeType(type);
		if (rangeType == null) {
			throw new APIException("Can't parse TimeRange: " + in);
		}
		return rangeType.getConverter().convert(parameter);
		
	}
}
