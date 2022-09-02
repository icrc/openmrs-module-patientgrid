package org.openmrs.module.patientgrid.filter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.patientgrid.PatientGridConstants.DATETIME_FORMAT;
import static org.openmrs.module.patientgrid.PatientGridConstants.DATE_FORMAT;
import static org.openmrs.module.reporting.common.BooleanOperator.AND;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.ObsPatientGridColumn;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.PatientGridColumnFilter;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PatientGridFilterUtilsTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void generateCohortDefinition_shouldAddAGenderCohortDefinitionForMalePatients() {
		final String gender = "M";
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("male", gender));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		GenderCohortDefinition def = (GenderCohortDefinition) PatientGridFilterUtils.generateCohortDefinition(grid);
		
		assertTrue(def.isMaleIncluded());
		assertFalse(def.isFemaleIncluded());
		assertFalse(def.isUnknownGenderIncluded());
	}
	
	@Test
	public void generateCohortDefinition_shouldAddAGenderCohortDefinitionForFemalePatients() {
		final String gender = "F";
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("female", gender));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		GenderCohortDefinition def = (GenderCohortDefinition) PatientGridFilterUtils.generateCohortDefinition(grid);
		
		assertTrue(def.isFemaleIncluded());
		assertFalse(def.isMaleIncluded());
		assertFalse(def.isUnknownGenderIncluded());
	}
	
	@Test
	public void generateCohortDefinition_shouldAddAGenderCohortDefinitionForBothMaleAndFemalePatients() {
		final String genderMale = "M";
		final String genderFemale = "F";
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("male", genderMale));
		column.addFilter(new PatientGridColumnFilter("female", genderFemale));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		GenderCohortDefinition def = (GenderCohortDefinition) PatientGridFilterUtils.generateCohortDefinition(grid);
		
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
		
		CompositionCohortDefinition def = (CompositionCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid);
		
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
		assertEquals(45.0, PatientGridFilterUtils.convert("45.0", Double.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToABoolean() {
		assertEquals(true, PatientGridFilterUtils.convert("true", Boolean.class));
		assertEquals(false, PatientGridFilterUtils.convert("false", Boolean.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToAConcept() {
		final String CONCEPT_UUID = "concept-uuid";
		PowerMockito.mockStatic(Context.class);
		Concept mockConcept = Mockito.mock(Concept.class);
		ConceptService mockConceptService = Mockito.mock(ConceptService.class);
		Mockito.when(Context.getConceptService()).thenReturn(mockConceptService);
		Mockito.when(mockConceptService.getConceptByUuid(CONCEPT_UUID)).thenReturn(mockConcept);
		
		assertEquals(mockConcept, PatientGridFilterUtils.convert(CONCEPT_UUID, Concept.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToADate() throws Exception {
		final String date = "2022-09-09";
		assertEquals(DATE_FORMAT.parse(date), PatientGridFilterUtils.convert(date, Date.class));
	}
	
	@Test
	public void convert_shouldConvertAStringToADatetime() throws Exception {
		final String date = "2022-09-09 14:00:05+00:00";
		assertEquals(DATETIME_FORMAT.parse(date), PatientGridFilterUtils.convert(date, Date.class));
	}
	
	@Test
	public void convert_shouldFailForANonSupportedValueType() {
		final Class clazz = Visit.class;
		expectedException.expect(APIException.class);
		expectedException.expectMessage(equalTo("Don't know how to convert operand value to type: " + clazz.getName()));
		
		PatientGridFilterUtils.convert("visit-uuid", clazz);
	}
	
	@Test
	public void generateCohortDefinition_shouldCohortDefinitionForANumericObsValue() {
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
		        .generateCohortDefinition(grid);
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueNumeric", def.getPropertyName());
		assertEquals(Arrays.asList(weight1), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCohortDefinitionForABooleanObsValue() {
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
		        .generateCohortDefinition(grid);
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueBoolean", def.getPropertyName());
		assertEquals(Arrays.asList(married), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCohortDefinitionForACodedObsValue() {
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
		PowerMockito.mockStatic(Context.class);
		Concept mockConcept = Mockito.mock(Concept.class);
		ConceptService mockConceptService = Mockito.mock(ConceptService.class);
		Mockito.when(Context.getConceptService()).thenReturn(mockConceptService);
		Mockito.when(mockConceptService.getConceptByUuid(conceptUuid)).thenReturn(mockConcept);
		
		ObsForLatestEncounterCohortDefinition def = (ObsForLatestEncounterCohortDefinition) PatientGridFilterUtils
		        .generateCohortDefinition(grid);
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueCoded", def.getPropertyName());
		assertEquals(Arrays.asList(mockConcept), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCohortDefinitionForADateObsValue() throws Exception {
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
		        .generateCohortDefinition(grid);
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueDatetime", def.getPropertyName());
		assertEquals(Arrays.asList(DATE_FORMAT.parse(date)), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCohortDefinitionForADatetimeObsValue() throws Exception {
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
		        .generateCohortDefinition(grid);
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueDatetime", def.getPropertyName());
		assertEquals(Arrays.asList(DATETIME_FORMAT.parse(date)), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldCohortDefinitionForATextObsValue() {
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
		        .generateCohortDefinition(grid);
		
		assertEquals(encounterType, def.getEncounterType());
		assertEquals(concept, def.getConcept());
		assertEquals("valueText", def.getPropertyName());
		assertEquals(Arrays.asList(nickName), def.getValues());
	}
	
	@Test
	public void generateCohortDefinition_shouldAddCohortDefinitionForAnObsColumnWithMultipleFilters() {
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
		        .generateCohortDefinition(grid);
		
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
	
}
