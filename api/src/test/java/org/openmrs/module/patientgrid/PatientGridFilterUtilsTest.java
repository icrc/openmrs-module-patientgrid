package org.openmrs.module.patientgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.APIException;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.BooleanOperator;

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
	public void generateCohortDefinition_shouldFilterByGenderAndFailForNoneSupportGenderValues() {
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("Other", "O"));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(Matchers.equalTo("Gender filter only supports M or F values as operands"));
		
		PatientGridFilterUtils.generateCohortDefinition(grid);
	}
	
	@Test
	public void generateCohortDefinition_shouldReturnNullIfTheGridHasNoFilters() {
		PatientGrid grid = new PatientGrid();
		grid.addColumn(new PatientGridColumn("gender", ColumnDatatype.GENDER));
		Assert.assertNull(PatientGridFilterUtils.generateCohortDefinition(grid));
	}
	
	@Test
	public void generateCohortDefinition_shouldGenerateACompositionCohortDefinitionForMultipleColumnFilters() {
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
		assertEquals(genderAtBirth + " " + BooleanOperator.AND + " " + identifiesAs, def.getCompositionString());
		assertEquals(GenderCohortDefinition.class, def.getSearches().get(genderAtBirth).getParameterizable().getClass());
		assertEquals(GenderCohortDefinition.class, def.getSearches().get(identifiesAs).getParameterizable().getClass());
	}
	
}
