package org.openmrs.module.patientgrid.filter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.*;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.filter.definition.AgeRangeAtLatestEncounterCohortDefinition;
import org.openmrs.module.patientgrid.filter.definition.LocationCohortDefinition;
import org.openmrs.module.patientgrid.filter.definition.ObsForLatestEncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.AgeRange;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.openmrs.module.patientgrid.PatientGridConstants.DATETIME_FORMAT;
import static org.openmrs.module.patientgrid.PatientGridConstants.DATE_FORMAT;
import static org.openmrs.module.patientgrid.PatientGridUtils.MAPPER;
import static org.openmrs.module.reporting.common.Age.Unit.YEARS;
import static org.openmrs.module.reporting.common.BooleanOperator.AND;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class PatientGridFilterUtilsTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void prepareMock() {
		PowerMockito.mockStatic(Context.class);
		AdministrationService mockAdminService = PowerMockito.mock(AdministrationService.class);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		PatientService mockPatientService = PowerMockito.mock(PatientService.class);
		when(Context.getPatientService()).thenReturn(mockPatientService);
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateAGenderCohortDefinitionForMalePatients() {
		final String gender = "M";
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("male", gender));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		GenderCohortDefinition def = (GenderCohortDefinition) PatientGridFilterUtils.generateCohortDefinition(grid)
		        .getObject();
		
		assertTrue(def.isMaleIncluded());
		assertFalse(def.isFemaleIncluded());
		assertFalse(def.isUnknownGenderIncluded());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateAGenderCohortDefinitionForFemalePatients() {
		final String gender = "F";
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("female", gender));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		GenderCohortDefinition def = (GenderCohortDefinition) PatientGridFilterUtils.generateCohortDefinition(grid)
		        .getObject();
		
		assertTrue(def.isFemaleIncluded());
		assertFalse(def.isMaleIncluded());
		assertFalse(def.isUnknownGenderIncluded());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateAGenderCohortDefinitionForBothMaleAndFemalePatients() {
		final String genderMale = "M";
		final String genderFemale = "F";
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("male", genderMale));
		column.addFilter(new PatientGridColumnFilter("female", genderFemale));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		GenderCohortDefinition def = (GenderCohortDefinition) PatientGridFilterUtils.generateCohortDefinition(grid)
		        .getObject();
		
		assertTrue(def.isFemaleIncluded());
		assertTrue(def.isMaleIncluded());
		assertFalse(def.isUnknownGenderIncluded());
	}
	
	@Test
	public void generateCohortDefinition_shouldFailForNoneSupportedGenderValues() {
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("Other", "O"));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(equalTo("Gender filter only supports M or F values as operands"));
		
		PatientGridFilterUtils.generateCohortDefinition(grid);
	}
	
	@Test
	public void generateCohortDefinition_shouldReturnNullIfTheGridHasNoFilters() {
		PatientGrid grid = new PatientGrid();
		grid.addColumn(new PatientGridColumn("gender", ColumnDatatype.GENDER));
		Assert.assertNull(PatientGridFilterUtils.generateCohortDefinition(grid));
	}
	
	@Test
	public void generateCohortDefinition_shouldGenerateACompositionCohortDefinitionForAGridWithMultipleFilteredColumns() {
		final String gender = "M";
		final String genderAtBirth = "genderAtBirth";
		final String identifiesAs = "identifiesAs";
		PatientGridColumn column1 = new PatientGridColumn(genderAtBirth, ColumnDatatype.GENDER);
		column1.addFilter(new PatientGridColumnFilter("male", gender));
		PatientGridColumn column2 = new PatientGridColumn(identifiesAs, ColumnDatatype.GENDER);
		column2.addFilter(new PatientGridColumnFilter("male", gender));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column1);
		grid.addColumn(column2);
		
		CompositionCohortDefinition def = (CompositionCohortDefinition) PatientGridFilterUtils.generateCohortDefinition(grid)
		        .getObject();
		
		assertEquals(2, def.getSearches().size());
		assertEquals(genderAtBirth + " " + AND + " " + identifiesAs, def.getCompositionString());
		assertEquals(GenderCohortDefinition.class, def.getSearches().get(genderAtBirth).getParameterizable().getClass());
		assertEquals(GenderCohortDefinition.class, def.getSearches().get(identifiesAs).getParameterizable().getClass());
	}
	
	@Test
	public void generateCohortDefinition_shouldFailIfAFilterIsFoundOnAColumnThatDoesNotSupportFiltering() {
		PatientGridColumn column = new PatientGridColumn("name", PatientGridColumn.ColumnDatatype.NAME);
		column.addFilter(new PatientGridColumnFilter("matches", "John"));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(equalTo("Don't know how to filter data for column type: " + column.getDatatype()));
		
		PatientGridFilterUtils.generateCohortDefinition(grid);
	}
	
	@Test
	public void generateCohortDefinition_shouldPassIfNoFiltersAreFoundOnAColumnThatDoesNotSupportFiltering() {
		PatientGrid grid = new PatientGrid();
		grid.addColumn(new PatientGridColumn("name", PatientGridColumn.ColumnDatatype.NAME));
		
		assertNull(PatientGridFilterUtils.generateCohortDefinition(grid));
	}
	
	@Test
	public void convert_shouldConvertAStringToADouble() {
		assertEquals(Double.valueOf(45.0), PatientGridUtils.convert("45.0", Double.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToAnInteger() {
		assertEquals(Integer.valueOf(45), PatientGridUtils.convert("45", Integer.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToABoolean() {
		assertEquals(true, PatientGridUtils.convert("true", Boolean.class));
		assertEquals(false, PatientGridUtils.convert("false", Boolean.class));
	}
	
	@Test
	public void convert_shouldConvertAUuidToAConcept() {
		final String conceptUuid = "concept-uuid";
		Concept mockConcept = Mockito.mock(Concept.class);
		ConceptService mockConceptService = Mockito.mock(ConceptService.class);
		Mockito.when(Context.getConceptService()).thenReturn(mockConceptService);
		Mockito.when(mockConceptService.getConceptByUuid(conceptUuid)).thenReturn(mockConcept);
		
		assertEquals(mockConcept, PatientGridUtils.convert(conceptUuid, Concept.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToADate() throws Exception {
		final String date = "2022-09-09";
		assertEquals(DATE_FORMAT.parse(date), PatientGridUtils.convert(date, Date.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToADatetime() throws Exception {
		final String date = "2022-09-09 14:00:05+00:00";
		assertEquals(DATETIME_FORMAT.parse(date), PatientGridUtils.convert(date, Date.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToAnAgeRange() throws Exception {
		final Integer minAge = 18;
		Map ageRange = Collections.singletonMap("minAge", minAge);
		
		AgeRange value = PatientGridUtils.convert(MAPPER.writeValueAsString(ageRange), AgeRange.class);
		
		assertEquals(minAge, value.getMinAge());
		assertEquals(YEARS, value.getMinAgeUnit());
		assertNull(value.getMaxAge());
		assertEquals(YEARS, value.getMaxAgeUnit());
		
		final Integer maxAge = 45;
		ageRange = Collections.singletonMap("maxAge", maxAge);
		
		value = PatientGridUtils.convert(MAPPER.writeValueAsString(ageRange), AgeRange.class);
		
		assertEquals(maxAge, value.getMaxAge());
		assertEquals(YEARS, value.getMaxAgeUnit());
		assertNull(value.getMinAge());
		assertEquals(YEARS, value.getMinAgeUnit());
		
		ageRange = new HashMap();
		ageRange.put("minAge", minAge);
		ageRange.put("maxAge", maxAge);
		
		value = PatientGridUtils.convert(MAPPER.writeValueAsString(ageRange), AgeRange.class);
		
		assertEquals(minAge, value.getMinAge());
		assertEquals(YEARS, value.getMinAgeUnit());
		assertEquals(maxAge, value.getMaxAge());
		assertEquals(YEARS, value.getMaxAgeUnit());
	}
	
	@Test
	public void convert_shouldFailForANonSupportedValueType() {
		final Class clazz = Visit.class;
		expectedException.expect(APIException.class);
		expectedException.expectMessage(equalTo("Don't know how to convert operand value to type: " + clazz.getName()));
		
		PatientGridUtils.convert("visit-uuid", clazz);
	}
	
	@Test
	public void convert_shouldConvertAUuidToALocation() {
		final String locationUuid = "location-uuid";
		Location mockLocation = Mockito.mock(Location.class);
		LocationService mockLocationService = Mockito.mock(LocationService.class);
		Mockito.when(Context.getLocationService()).thenReturn(mockLocationService);
		Mockito.when(mockLocationService.getLocationByUuid(locationUuid)).thenReturn(mockLocation);
		
		assertEquals(mockLocation, PatientGridUtils.convert(locationUuid, Location.class));
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateACohortDefinitionForANumericObsValue() {
		final Double weight1 = 165.0;
		ObsPatientGridColumn column = new ObsPatientGridColumn("weight", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.NUMERIC_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("is 165", weight1.toString()));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueNumeric", def.getPropertyName());
		assertEquals(Arrays.asList(weight1), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateACohortDefinitionForABooleanObsValue() {
		final Boolean married = true;
		ObsPatientGridColumn column = new ObsPatientGridColumn("married", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.BOOLEAN_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("is married", married.toString()));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueBoolean", def.getPropertyName());
		assertEquals(Arrays.asList(married), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateACohortDefinitionForACodedObsValue() {
		final String conceptUuid = "concept-uuid";
		ObsPatientGridColumn column = new ObsPatientGridColumn("maritalStatus", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.CODED_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("maritalStatus", conceptUuid));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		Concept mockConcept = Mockito.mock(Concept.class);
		ConceptService mockConceptService = Mockito.mock(ConceptService.class);
		Mockito.when(Context.getConceptService()).thenReturn(mockConceptService);
		Mockito.when(mockConceptService.getConceptByUuid(conceptUuid)).thenReturn(mockConcept);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueCoded", def.getPropertyName());
		assertEquals(Arrays.asList(mockConcept), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateACohortDefinitionForADateObsValue() throws Exception {
		final String date = "2022-09-09";
		ObsPatientGridColumn column = new ObsPatientGridColumn("lastVisitDate", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.DATE_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("lastVisitDate", date));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueDatetime", def.getPropertyName());
		assertEquals(Arrays.asList(DATE_FORMAT.parse(date)), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateACohortDefinitionForADatetimeObsValue() throws Exception {
		final String date = "2022-09-09 14:00:05+00:00";
		ObsPatientGridColumn column = new ObsPatientGridColumn("lastEncDatetime", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.DATETIME_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("lastEncDatetime", date));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueDatetime", def.getPropertyName());
		assertEquals(Arrays.asList(DATETIME_FORMAT.parse(date)), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateACohortDefinitionForATextObsValue() {
		final String nickName = "dev";
		ObsPatientGridColumn column = new ObsPatientGridColumn("nickName", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.TEXT_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("nickName", nickName));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueText", def.getPropertyName());
		assertEquals(Arrays.asList(nickName), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateACohortDefinitionForAnObsColumnWithMultipleFilters() {
		final Double weight1 = 165.0;
		final Double weight2 = 190.5;
		ObsPatientGridColumn column = new ObsPatientGridColumn("weight", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.NUMERIC_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("is 165", weight1.toString()));
		column.addFilter(new PatientGridColumnFilter("is 190.5", weight2.toString()));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueNumeric", def.getPropertyName());
		assertEquals(Arrays.asList(weight1, weight2), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldFailForAnObsColumnWithValuesOfANonSupportedConceptDatatype() {
		ObsPatientGridColumn column = new ObsPatientGridColumn("weight", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.COMPLEX_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		column.addFilter(new PatientGridColumnFilter("is some value", "some-complex-data"));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(equalTo("Don't know how to filter obs data of datatype: " + concept.getDatatype()));
		
		PatientGridFilterUtils.generateCohortDefinition(grid);
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateALocationCohortDefinition() {
		final String locationUuid = "location-uuid";
		PatientGridColumn column = new PatientGridColumn("location", ColumnDatatype.ENC_LOCATION);
		column.addFilter(new PatientGridColumnFilter("equal location", locationUuid));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(new AgeAtEncounterPatientGridColumn("age", new EncounterType()));
		grid.addColumn(column);
		Location mockLocation = Mockito.mock(Location.class);
		LocationService mockLocationService = Mockito.mock(LocationService.class);
		Mockito.when(Context.getLocationService()).thenReturn(mockLocationService);
		Mockito.when(mockLocationService.getLocationByUuid(locationUuid)).thenReturn(mockLocation);
		
		AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertTrue(def.getLocationCohortDefinition().getLocations().contains(mockLocation));
		assertFalse(def.getLocationCohortDefinition().getCountry());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateALocationCohortDefinitionForAColumnWithMultipleFilters() {
		final String locationUuid1 = "location-uuid1";
		final String locationUuid2 = "location-uuid2";
		PatientGridColumn column = new PatientGridColumn("location", ColumnDatatype.ENC_LOCATION);
		column.addFilter(new PatientGridColumnFilter("equal location1", locationUuid1));
		column.addFilter(new PatientGridColumnFilter("equal location2", locationUuid2));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(new AgeAtEncounterPatientGridColumn("age", new EncounterType()));
		grid.addColumn(column);
		Location mockLocation1 = Mockito.mock(Location.class);
		Location mockLocation2 = Mockito.mock(Location.class);
		LocationService mockLocationService = Mockito.mock(LocationService.class);
		Mockito.when(Context.getLocationService()).thenReturn(mockLocationService);
		Mockito.when(mockLocationService.getLocationByUuid(locationUuid1)).thenReturn(mockLocation1);
		Mockito.when(mockLocationService.getLocationByUuid(locationUuid2)).thenReturn(mockLocation2);
		
		AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertTrue(def.getLocationCohortDefinition().getLocations().contains(mockLocation1));
		assertTrue(def.getLocationCohortDefinition().getLocations().contains(mockLocation2));
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateALocationCohortDefinitionForCountry() {
		final String countryLocationUuid = "country-location-uuid";
		
		PatientGrid grid = new PatientGrid();
		grid.addColumn(new AgeAtEncounterPatientGridColumn("age", new EncounterType()));
		PatientGridColumn column = new PatientGridColumn("country", ColumnDatatype.ENC_COUNTRY);
		column.addFilter(new PatientGridColumnFilter("equal country", countryLocationUuid));
		grid.addColumn(column);
		Location mockLocation = Mockito.mock(Location.class);
		LocationService mockLocationService = Mockito.mock(LocationService.class);
		Mockito.when(Context.getLocationService()).thenReturn(mockLocationService);
		Mockito.when(mockLocationService.getLocationByUuid(countryLocationUuid)).thenReturn(mockLocation);
		
		AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertTrue(def.getLocationCohortDefinition().getLocations().contains(mockLocation));
		assertTrue(def.getLocationCohortDefinition().getCountry());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateALocationCohortDefinitionForCountryForColumnWithMultipleFilters() {
		final String countryLocationUuid1 = "country-location-uuid1";
		final String countryLocationUuid2 = "country-location-uuid2";
		PatientGrid grid = new PatientGrid();
		grid.addColumn(new AgeAtEncounterPatientGridColumn("age", new EncounterType()));
		PatientGridColumn column = new PatientGridColumn("location", ColumnDatatype.ENC_COUNTRY);
		column.addFilter(new PatientGridColumnFilter("equal country1", countryLocationUuid1));
		column.addFilter(new PatientGridColumnFilter("equal country2", countryLocationUuid2));
		grid.addColumn(column);
		Location mockLocation1 = Mockito.mock(Location.class);
		Location mockLocation2 = Mockito.mock(Location.class);
		LocationService mockLocationService = Mockito.mock(LocationService.class);
		Mockito.when(Context.getLocationService()).thenReturn(mockLocationService);
		Mockito.when(mockLocationService.getLocationByUuid(countryLocationUuid1)).thenReturn(mockLocation1);
		Mockito.when(mockLocationService.getLocationByUuid(countryLocationUuid2)).thenReturn(mockLocation2);
		
		//not perfect as the location created a AgeRangeAtLatestEncounterCohortDefinition
		AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertTrue(def.getLocationCohortDefinition().getLocations().contains(mockLocation1));
		assertTrue(def.getLocationCohortDefinition().getLocations().contains(mockLocation2));
		assertTrue(def.getLocationCohortDefinition().getCountry());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateAnAgeAtLatestEncounterCohortDefinitionForAge() {
		final Integer age = 45;
		final EncounterType encounterType = new EncounterType();
		AgeAtEncounterPatientGridColumn column = new AgeAtEncounterPatientGridColumn("age", encounterType);
		column.addFilter(new PatientGridColumnFilter("equal age", age.toString()));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(1, def.getAgeRanges().size());
		AgeRange ageRange = def.getAgeRanges().get(0);
		assertEquals(age, ageRange.getMinAge());
		assertEquals(YEARS, ageRange.getMinAgeUnit());
		assertEquals(age, ageRange.getMaxAge());
		assertEquals(YEARS, ageRange.getMaxAgeUnit());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateAnAgeAtLatestEncounterCohortDefinitionForMultipleAgeFilters() {
		final Integer age1 = 45;
		final Integer age2 = 47;
		final EncounterType encounterType = new EncounterType();
		AgeAtEncounterPatientGridColumn column = new AgeAtEncounterPatientGridColumn("age", encounterType);
		column.addFilter(new PatientGridColumnFilter("equal age1", age1.toString()));
		column.addFilter(new PatientGridColumnFilter("equal age2", age2.toString()));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(2, def.getAgeRanges().size());
		AgeRange ageRange = def.getAgeRanges().get(0);
		assertEquals(age1, ageRange.getMinAge());
		assertEquals(YEARS, ageRange.getMinAgeUnit());
		assertEquals(age1, ageRange.getMaxAge());
		assertEquals(YEARS, ageRange.getMaxAgeUnit());
		
		ageRange = def.getAgeRanges().get(1);
		assertEquals(age2, ageRange.getMinAge());
		assertEquals(YEARS, ageRange.getMinAgeUnit());
		assertEquals(age2, ageRange.getMaxAge());
		assertEquals(YEARS, ageRange.getMaxAgeUnit());
	}
	
	@Test
	public void generateCohortDefinition_shouldCreateAnAgeAtLatestEncounterCohortDefinitionForAgeRange() throws Exception {
		final Integer minAge = 45;
		final Integer maxAge = 50;
		Map ageRangeMap = new HashMap();
		ageRangeMap.put("minAge", minAge);
		ageRangeMap.put("maxAge", maxAge);
		final EncounterType encounterType = new EncounterType();
		AgeAtEncounterPatientGridColumn column = new AgeAtEncounterPatientGridColumn("age", encounterType, true);
		column.addFilter(new PatientGridColumnFilter("equal age", MAPPER.writeValueAsString(ageRangeMap)));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		AgeRangeAtLatestEncounterCohortDefinition def = (AgeRangeAtLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid).getObject();
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(1, def.getAgeRanges().size());
		AgeRange ageRange = def.getAgeRanges().get(0);
		assertEquals(minAge, ageRange.getMinAge());
		assertEquals(YEARS, ageRange.getMinAgeUnit());
		assertEquals(maxAge, ageRange.getMaxAge());
		assertEquals(YEARS, ageRange.getMaxAgeUnit());
	}
	
	@Test
	public void generateCohortDefinition_shouldFailIfNoConceptMatchesTheSpecifiedUuid() {
		final String conceptUuid = "concept-uuid";
		ObsPatientGridColumn column = new ObsPatientGridColumn("maritalStatus", null, null);
		Concept concept = new Concept();
		ConceptDatatype datatype = new ConceptDatatype();
		datatype.setUuid(ConceptDatatype.CODED_UUID);
		concept.setDatatype(datatype);
		column.setConcept(concept);
		EncounterType encounterType = new EncounterType();
		column.setEncounterType(encounterType);
		column.addFilter(new PatientGridColumnFilter("maritalStatus", conceptUuid));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		ConceptService mockConceptService = Mockito.mock(ConceptService.class);
		Mockito.when(Context.getConceptService()).thenReturn(mockConceptService);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(equalTo("No concept found with uuid: " + conceptUuid));
		
		PatientGridFilterUtils.generateCohortDefinition(grid);
	}
	
	@Test
	public void generateCohortDefinition_shouldFailIfNoLocationMatchesTheSpecifiedUuid() {
		final String countryLocationUuid = "country-location-uuid";
		PatientGridColumn column = new PatientGridColumn("country", ColumnDatatype.ENC_COUNTRY);
		column.addFilter(new PatientGridColumnFilter("equal country", countryLocationUuid));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		LocationService mockLocationService = Mockito.mock(LocationService.class);
		Mockito.when(Context.getLocationService()).thenReturn(mockLocationService);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(equalTo("No location found with uuid: " + countryLocationUuid));
		
		PatientGridFilterUtils.generateCohortDefinition(grid);
	}
	
}
