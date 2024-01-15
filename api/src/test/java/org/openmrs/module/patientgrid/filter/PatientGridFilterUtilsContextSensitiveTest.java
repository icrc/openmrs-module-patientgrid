package org.openmrs.module.patientgrid.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.PatientGridColumn;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PatientGridFilterUtilsContextSensitiveTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private PatientGridService service;

	@Autowired
	@Qualifier("patientService")
	private PatientService patientService;

	@Before
	public void setup() {
		executeDataSet("entityBasisMaps.xml");
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}

	@Test
	public void filterPatients_shouldReturnACohortOfPatientsMatchingTheColumnFilters() throws Exception {
		final Integer patientId2 = 2;
		final Integer patientId6 = 6;
		final Integer patientId7 = 7;//Female
		//The filters are for male and (married or single) patients
		PatientGrid patientGrid = service.getPatientGrid(2);
		boolean hasFilteredColumns = false;
		for (PatientGridColumn column : patientGrid.getColumns()) {
			if (!column.getFilters().isEmpty()) {
				hasFilteredColumns = true;
				break;
			}
		}
		assertTrue(hasFilteredColumns);
		assertTrue(patientGrid.getCohort().getActiveMemberships().isEmpty());
		Cohort cohort = new Cohort(Arrays.asList(patientId2, patientId6, patientId7));
		cohort.setName("test");
		cohort.setDescription("test");
		Context.getCohortService().saveCohort(cohort);
		patientGrid.setCohort(cohort);

		ObjectWithDateRange<Cohort> filteredCohort = PatientGridFilterUtils.filterPatients(patientGrid, null, null);

		assertEquals(2, filteredCohort.getObject().size());
		assertNotNull(filteredCohort.getObject().getActiveMembership(patientService.getPatient(patientId2)));
		assertNotNull(filteredCohort.getObject().getActiveMembership(patientService.getPatient(patientId6)));

		assertEquals(
		    "{\"code\":\"customDaysInclusive\",\"fromDate\":\"2022-04-01 00:00:00\",\"toDate\":\"2022-12-31 00:00:00\"}",
		    filteredCohort.getDateRange().getOperand());
	}

	@Test
	public void filterPatients_shouldNotReturnNullIfNoFiltersAreFoundAsFilterOnEncounterTypeByDefault() throws Exception {
		PatientGrid patientGrid = service.getPatientGrid(1);
		boolean hasFilteredColumns = false;
		for (PatientGridColumn column : patientGrid.getColumns()) {
			if (!column.getFilters().isEmpty()) {
				hasFilteredColumns = true;
				break;
			}
		}
		assertTrue(hasFilteredColumns);

		Cohort cohort = PatientGridFilterUtils.filterPatients(patientGrid, null, null).getObject();
		assertEquals(4, cohort.getMemberships().size());
	}

}
