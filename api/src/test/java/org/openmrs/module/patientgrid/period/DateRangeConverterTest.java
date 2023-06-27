package org.openmrs.module.patientgrid.period;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.impl.DefaultMessageSourceServiceImpl;
import org.openmrs.module.patientgrid.cache.DiskCache;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class DateRangeConverterTest {
	
	//  {"fromDate":"2023-02-13 00:00:00","toDate":"2023-02-14 00:00:00"}
	
	private final static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssXXX");
	
	// 7h before
	private final String asiaTimeZone = "Asia/Phnom_Penh";
	
	private final String frenchTimeZone = "Europe/Paris";
	
	private final String utcTimeZone = "UTC";
	
	@Before
	public void prepareFormatter() {
		PowerMockito.mockStatic(Context.class);
		PatientService mockPatientService = PowerMockito.mock(PatientService.class);
		when(Context.getPatientService()).thenReturn(mockPatientService);
		dateTimeFormat.setTimeZone(TimeZone.getTimeZone(utcTimeZone));
	}
	
	static String createJson(String code, String from, String to) {
		return String.format("{\"code\":\"%s\",\"fromDate\":\"%s 00:00:00\",\"toDate\":\"%s 00:00:00\"}", code, from, to);
	}
	
	static String createJson(String code) {
		return String.format("{\"code\":\"%s\",\"fromDate\":\"\",\"toDate\":\"\"}", code);
	}
	
	@Test
	public void createJson_shouldReturnJson() {
		Assert.assertEquals("{\"code\":\"today\",\"fromDate\":\"\",\"toDate\":\"\"}", createJson("today"));
		Assert.assertEquals(
		    "{\"code\":\"customDaysInclusive\",\"fromDate\":\"2022-01-02 00:00:00\",\"toDate\":\"2022-01-03 00:00:00\"}",
		    createJson("customDaysInclusive", "2022-01-02", "2022-01-03"));
		
	}
	
	private String formatDate(Date in) {
		return dateTimeFormat.format(in);
	}
	
	static DateTime createDate(String in) throws ParseException {
		return new DateTime(dateTimeFormat.parse(in).getTime());
	}
	
	@Test
	public void extractDateOnly_shouldReturnDateInLocale() {
		Assert.assertEquals("2022-01-02", DateRangeConverter.extractDateOnly("2022-01-02 00:00:00"));
	}
	
	@Test
	public void creationDateRangeConverter_shouldParseUserTimezone() {
		
		DateRangeConverter converter = new DateRangeConverter(frenchTimeZone);
		Assert.assertEquals(frenchTimeZone, converter.getUserTimeZone().getID());
	}
	
	@Test
	public void getDisplay_shouldReturnTranslatedString() {
		PowerMockito.mockStatic(Context.class);
		MessageSourceService messageSourceService = PowerMockito.mock(MessageSourceService.class);
		when(Context.getMessageSourceService()).thenReturn(DefaultMessageSourceServiceImpl.getInstance());
		
		Assert.assertEquals("Today", DateRangeConverter.getDisplay(createJson("today"), Locale.ENGLISH));
	}
	
	@Test
	public void getDisplay_shouldReturnTranslatedStringForAllDateRange() {
		PowerMockito.mockStatic(Context.class);
		MessageSourceService messageSourceService = PowerMockito.mock(MessageSourceService.class);
		when(Context.getMessageSourceService()).thenReturn(DefaultMessageSourceServiceImpl.getInstance());
		
		Arrays.stream(DateRangeType.values()).forEach(dateRangeType -> {
			String json = createJson(dateRangeType.name().toLowerCase());
			Assert.assertNotEquals(dateRangeType.name() + " should be defined in messages.properties", json,
			    DateRangeConverter.getDisplay(json, Locale.ENGLISH));
		});
	}
	
	@Test
	public void convertCustomDaysInclusive_shouldReturnExpectedSpecificDateRangeForUserInParis() {
		//setup
		final String oldTimeZone = System.getProperty("user.timezone");
		System.setProperty("user.timezone", asiaTimeZone);//to test that current timezone has no impact
		final DateRangeConverter converter = new DateRangeConverter(frenchTimeZone);
		
		//play
		final DateRange customDaysInclusive = converter
		        .convert(createJson("customDaysInclusive", "2023-01-10", "2023-01-11"), null);
		
		//verify
		Assert.assertEquals("2023-01-09 23:00:00Z", formatDate(customDaysInclusive.getFromInServerTz()));
		Assert.assertEquals("2023-01-11 22:59:59Z", formatDate(customDaysInclusive.getToInServerTz()));
		
		//reset timezone
		System.setProperty("user.timezone", oldTimeZone);
		
	}
	
	@Test
	public void convertCustomDaysInclusive_shouldReturnSpecificDateRangeForUserInPhnomPenh() {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(asiaTimeZone);
		
		//play
		final DateRange customDaysInclusive = converter
		        .convert(createJson("customDaysInclusive", "2023-01-11", "2023-01-12"), null);
		//verify
		Assert.assertEquals("2023-01-10 17:00:00Z", formatDate(customDaysInclusive.getFromInServerTz()));
		Assert.assertEquals("2023-01-12 16:59:59Z", formatDate(customDaysInclusive.getToInServerTz()));
		
	}
	
	@Test
	public void convertToday_shouldReturnSpecificDateRangeWithNoConversionForUserInPhnomPenh() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(asiaTimeZone);
		DateTime now = createDate("2023-01-31 14:25:10Z"); // jan 31 at 21:25 in Asia
		DateTime tomorrow = createDate("2023-01-31 17:00:20Z"); // feb 1 in Asia
		//play
		
		final DateRange today = converter.convert(createJson("today"), now);
		final DateRange previousDay = converter.convert(createJson("today"), tomorrow);
		
		//verify
		//jan 31 in Asia
		Assert.assertEquals("2023-01-30 17:00:00Z", formatDate(today.getFromInServerTz()));
		Assert.assertEquals("2023-01-31 16:59:59Z", formatDate(today.getToInServerTz()));
		
		//feb 1 in Asia
		Assert.assertEquals("2023-01-31 17:00:00Z", formatDate(previousDay.getFromInServerTz()));
		Assert.assertEquals("2023-02-01 16:59:59Z", formatDate(previousDay.getToInServerTz()));
		
	}
	
	@Test
	public void convertYesterday_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		DateTime now = createDate("2023-01-31 18:22:00Z");
		
		//play
		
		final DateRange customDaysInclusive = converter.convert(createJson("yesterday"), now);
		
		//verify
		Assert.assertEquals("2023-01-30 00:00:00Z", formatDate(customDaysInclusive.getFromInServerTz()));
		Assert.assertEquals("2023-01-30 23:59:59Z", formatDate(customDaysInclusive.getToInServerTz()));
		
	}
	
	@Test
	public void convertLastSevenDays_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange sevenDays = converter.convert(createJson("lastSevenDays"), createDate("2023-01-02 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2022-12-27 00:00:00Z", formatDate(sevenDays.getFromInServerTz()));
		Assert.assertEquals("2023-01-02 23:59:59Z", formatDate(sevenDays.getToInServerTz()));
		
	}
	
	@Test
	public void convertLastThirtyDays_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange lastThirtyDays = converter.convert(createJson("lastThirtyDays"), createDate("2023-01-30 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2023-01-01 00:00:00Z", formatDate(lastThirtyDays.getFromInServerTz()));
		Assert.assertEquals("2023-01-30 23:59:59Z", formatDate(lastThirtyDays.getToInServerTz()));
		
	}
	
	@Test
	public void convertWeekToDate_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange weekToDateFromMonday = converter.convert(createJson("weekToDate"),
		    createDate("2023-01-30 18:22:00Z"));
		final DateRange weekToDateFromSunday = converter.convert(createJson("weekToDate"),
		    createDate("2023-02-05 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2023-01-30 00:00:00Z", formatDate(weekToDateFromMonday.getFromInServerTz()));
		Assert.assertEquals("2023-01-30 23:59:59Z", formatDate(weekToDateFromMonday.getToInServerTz()));
		Assert.assertEquals("2023-01-30 00:00:00Z", formatDate(weekToDateFromSunday.getFromInServerTz()));
		Assert.assertEquals("2023-02-05 23:59:59Z", formatDate(weekToDateFromSunday.getToInServerTz()));
		
	}
	
	@Test
	public void convertMonthToDate_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange monthToDate = converter.convert(createJson("monthToDate"), createDate("2023-02-20 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2023-02-01 00:00:00Z", formatDate(monthToDate.getFromInServerTz()));
		Assert.assertEquals("2023-02-20 23:59:59Z", formatDate(monthToDate.getToInServerTz()));
		
	}
	
	@Test
	public void convertQuarterToDate_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange quarterToDateFirstQuarter = converter.convert(createJson("quarterToDate"),
		    createDate("2023-03-31 18:22:00Z"));
		final DateRange quarterToDateSecondQuarter = converter.convert(createJson("quarterToDate"),
		    createDate("2023-04-20 18:22:00Z"));
		final DateRange quarterToDateThirdQuarter = converter.convert(createJson("quarterToDate"),
		    createDate("2023-07-12 18:22:00Z"));
		final DateRange quarterToDateFourthQuarter = converter.convert(createJson("quarterToDate"),
		    createDate("2023-11-20 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2023-01-01 00:00:00Z", formatDate(quarterToDateFirstQuarter.getFromInServerTz()));
		Assert.assertEquals("2023-03-31 23:59:59Z", formatDate(quarterToDateFirstQuarter.getToInServerTz()));
		
		Assert.assertEquals("2023-04-01 00:00:00Z", formatDate(quarterToDateSecondQuarter.getFromInServerTz()));
		Assert.assertEquals("2023-04-20 23:59:59Z", formatDate(quarterToDateSecondQuarter.getToInServerTz()));
		
		Assert.assertEquals("2023-07-01 00:00:00Z", formatDate(quarterToDateThirdQuarter.getFromInServerTz()));
		Assert.assertEquals("2023-07-12 23:59:59Z", formatDate(quarterToDateThirdQuarter.getToInServerTz()));
		
		Assert.assertEquals("2023-10-01 00:00:00Z", formatDate(quarterToDateFourthQuarter.getFromInServerTz()));
		Assert.assertEquals("2023-11-20 23:59:59Z", formatDate(quarterToDateFourthQuarter.getToInServerTz()));
		
	}
	
	@Test
	public void convertYearToDate_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange weekToDateFromSunday = converter.convert(createJson("yearToDate"),
		    createDate("2023-02-20 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2023-01-01 00:00:00Z", formatDate(weekToDateFromSunday.getFromInServerTz()));
		Assert.assertEquals("2023-02-20 23:59:59Z", formatDate(weekToDateFromSunday.getToInServerTz()));
		
	}
	
	@Test
	public void convertPreviousWeek_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange previousWeek = converter.convert(createJson("previousWeek"), createDate("2023-02-22 18:22:00Z"));
		final DateRange previousWeekInPreviousYear = converter.convert(createJson("previousWeek"),
		    createDate("2023-01-02 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2023-02-13 00:00:00Z", formatDate(previousWeek.getFromInServerTz()));
		Assert.assertEquals("2023-02-19 23:59:59Z", formatDate(previousWeek.getToInServerTz()));
		
		Assert.assertEquals("2022-12-26 00:00:00Z", formatDate(previousWeekInPreviousYear.getFromInServerTz()));
		Assert.assertEquals("2023-01-01 23:59:59Z", formatDate(previousWeekInPreviousYear.getToInServerTz()));
		
	}
	
	@Test
	public void convertPreviousMonth_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange previousMonth = converter.convert(createJson("previousMonth"), createDate("2023-03-22 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2023-02-01 00:00:00Z", formatDate(previousMonth.getFromInServerTz()));
		Assert.assertEquals("2023-02-28 23:59:59Z", formatDate(previousMonth.getToInServerTz()));
	}
	
	@Test
	public void convertPreviousQuarter_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange previousWeek = converter.convert(createJson("previousQuarter"), createDate("2023-03-22 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2022-10-01 00:00:00Z", formatDate(previousWeek.getFromInServerTz()));
		Assert.assertEquals("2022-12-31 23:59:59Z", formatDate(previousWeek.getToInServerTz()));
	}
	
	@Test
	public void convertPreviousYear_shouldReturnSpecificDateRange() throws ParseException {
		
		//setup
		final DateRangeConverter converter = new DateRangeConverter(utcTimeZone);
		
		//play
		final DateRange previousWeek = converter.convert(createJson("previousYear"), createDate("2023-03-22 18:22:00Z"));
		
		//verify
		Assert.assertEquals("2022-01-01 00:00:00Z", formatDate(previousWeek.getFromInServerTz()));
		Assert.assertEquals("2022-12-31 23:59:59Z", formatDate(previousWeek.getToInServerTz()));
	}
	
}
