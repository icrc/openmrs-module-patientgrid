package org.openmrs.module.patientgrid.web.rest.v1_0.search;

import static org.junit.Assert.assertEquals;
import static org.openmrs.module.patientgrid.web.rest.v1_0.search.EncounterHistorySearchHandler.*;
import static org.openmrs.module.webservices.rest.web.RestConstants.REQUEST_PROPERTY_FOR_SEARCH_ID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest.Parameter;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;

public class EncounterHistorySearchHandlerTest extends RestControllerTestUtils {

	private static final String URI = "encounter";

	@Rule
	public ExpectedException ee = ExpectedException.none();

	@Before
	public void setupBasePatientGridRestControllerTest() {
		executeDataSet("entityBasisMaps.xml");
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}

	@Test
	public void shouldReturnAllEncountersOfTheSpecifiedTypeForAPatient() throws Exception {
		Parameter handler = new Parameter(REQUEST_PROPERTY_FOR_SEARCH_ID, SEARCH_CONFIG_NAME);
		Parameter patient = new Parameter(PARAM_PATIENT, "da7f524f-27ce-4bb2-86d6-6d1d05312bd5");
		Parameter encType = new Parameter(PARAM_ENC_TYPE, "19218f76-6c39-45f4-8efa-4c5c6c199f50");
		Parameter patientGridUuid = new Parameter(PARAM_PATIENT_GRID_UUID, "1d6c993e-c2cc-11de-8d13-0010c6dffd0a");

		SimpleObject result = deserialize(handle(newGetRequest(URI, handler, patient, encType, patientGridUuid)));

		assertEquals(3, Util.getResultsSize(result));
	}

	@Test(expected = ObjectNotFoundException.class)
	public void shouldFailIfNoMatchingPatientIsFound() throws Exception {
		Parameter handler = new Parameter(REQUEST_PROPERTY_FOR_SEARCH_ID, SEARCH_CONFIG_NAME);
		Parameter patient = new Parameter(PARAM_PATIENT, "bad-uuid");
		Parameter encType = new Parameter(PARAM_ENC_TYPE, "19218f76-6c39-45f4-8efa-4c5c6c199f50");

		handle(newGetRequest(URI, handler, patient, encType));
	}

	@Test(expected = ObjectNotFoundException.class)
	public void shouldFailIfNoMatchingEncounterTypeIsFound() throws Exception {
		Parameter handler = new Parameter(REQUEST_PROPERTY_FOR_SEARCH_ID, SEARCH_CONFIG_NAME);
		Parameter patient = new Parameter(PARAM_PATIENT, "da7f524f-27ce-4bb2-86d6-6d1d05312bd5");
		Parameter encType = new Parameter(PARAM_ENC_TYPE, "bad-uuid");

		handle(newGetRequest(URI, handler, patient, encType));
	}

}
