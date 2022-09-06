package org.openmrs.module.patientgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PatientGridFilterUtilsTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientGridService service;
	
	@Autowired
	@Qualifier("patientService")
	private PatientService patientService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
		executeDataSet("entityBasisMaps.xml");
	}
	
	@Test
	public void filterPatients_shouldFilterByGenderAndReturnOnlyTheMalePatients() throws Exception {
		final String gender = "M";
		final int expectedPatientId2 = 2;
		final int expectedPatientId6 = 6;
		assertEquals(gender, patientService.getPatient(expectedPatientId2).getGender());
		assertEquals(gender, patientService.getPatient(expectedPatientId6).getGender());
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("male", gender));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		Cohort cohort = PatientGridFilterUtils.filterPatients(grid);
		
		assertEquals(2, cohort.activeMembershipSize());
		assertTrue(cohort.contains(expectedPatientId2));
		assertTrue(cohort.contains(expectedPatientId6));
	}
	
	@Test
	public void filterPatients_shouldFilterByGenderAndReturnOnlyTheFemalePatients() throws Exception {
		final String gender = "F";
		final int expectedPatientId7 = 7;
		final int expectedPatientId8 = 8;
		assertEquals(gender, patientService.getPatient(expectedPatientId7).getGender());
		assertEquals(gender, patientService.getPatient(expectedPatientId8).getGender());
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("female", gender));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		Cohort cohort = PatientGridFilterUtils.filterPatients(grid);
		
		assertEquals(2, cohort.activeMembershipSize());
		assertTrue(cohort.contains(expectedPatientId7));
		assertTrue(cohort.contains(expectedPatientId8));
	}
	
	@Test
	public void filterPatients_shouldFilterByGenderAndReturnBothMaleAndFemalePatients() throws Exception {
		final String genderMale = "M";
		final String genderFemale = "F";
		final int expectedPatientId2 = 2;
		final int expectedPatientId6 = 6;
		final int expectedPatientId7 = 7;
		final int expectedPatientId8 = 8;
		assertEquals(genderMale, patientService.getPatient(expectedPatientId2).getGender());
		assertEquals(genderMale, patientService.getPatient(expectedPatientId6).getGender());
		assertEquals(genderFemale, patientService.getPatient(expectedPatientId7).getGender());
		assertEquals(genderFemale, patientService.getPatient(expectedPatientId8).getGender());
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("male", genderMale));
		column.addFilter(new PatientGridColumnFilter("female", genderFemale));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		
		Cohort cohort = PatientGridFilterUtils.filterPatients(grid);
		
		assertEquals(4, cohort.activeMembershipSize());
		assertTrue(cohort.contains(expectedPatientId2));
		assertTrue(cohort.contains(expectedPatientId6));
		assertTrue(cohort.contains(expectedPatientId7));
		assertTrue(cohort.contains(expectedPatientId8));
	}
	
	@Test
	public void filterPatients_shouldFilterByGenderAndFailForNoneSupportGenderValues() throws Exception {
		PatientGridColumn column = new PatientGridColumn("gender", ColumnDatatype.GENDER);
		column.addFilter(new PatientGridColumnFilter("Other", "O"));
		PatientGrid grid = new PatientGrid();
		grid.addColumn(column);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(Matchers.equalTo("Gender filter only supports M or F values as operands"));
		
		PatientGridFilterUtils.filterPatients(grid);
	}
	
}
