package org.openmrs.module.patientgrid.converter;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.AgeRange;

public class PatientGridAgeRangeConverterTest {

	private PatientGridAgeRangeConverter converter;

	@Before
	public void setup() {
		converter = new PatientGridAgeRangeConverter();
		AgeRange below18 = new AgeRange(0, 17);
		AgeRange above18 = new AgeRange(18, null);
		converter.addAgeRange(below18);
		converter.addAgeRange(above18);
	}

	@Test
	public void convert_shouldReturnTheAgeRange() throws Exception {

		Date birthDate = PatientGridConstants.DATE_FORMAT.parse("2000-05-05");
		Date currentDate = PatientGridConstants.DATE_FORMAT.parse("2020-06-06");
		Assert.assertEquals("18+", converter.convert(new Age(birthDate, currentDate)));
	}

	@Test
	public void convert_shouldReturnNullForANullValue() {
		Assert.assertNull(converter.convert(null));
	}
}
