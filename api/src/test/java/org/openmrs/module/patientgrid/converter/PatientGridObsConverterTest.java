package org.openmrs.module.patientgrid.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientGridObsConverterTest extends BaseModuleContextSensitiveTest {
	
	private PatientGridObsConverter converter = new PatientGridObsConverter();
	
	@Before
	public void setup() {
		executeDataSet("entityBasisMaps.xml");
		executeDataSet("patientGrids.xml");
		executeDataSet("patientGridsTestData.xml");
	}
	
	@Test
	public void shouldReturnNullForANullObs() {
		Assert.assertNull(converter.convert(null));
	}
	
	@Test
	public void shouldReturnTheConvertedObs() {
		Obs obs = Context.getObsService().getObs(1002);
		final String formNamespace = "form-name-space";
		final String formFieldPath = "form-field-path";
		obs.setFormField(formNamespace, formFieldPath);
		Form form = Context.getFormService().getForm(1);
		obs.getEncounter().setForm(form);
		Map convertedObs = (Map) converter.convert(obs);
		assertEquals(obs.getUuid(), convertedObs.get("uuid"));
		assertEquals(obs.getConcept().getUuid(), convertedObs.get(PatientGridConstants.PROP_CONCEPT));
		assertEquals(Double.valueOf(82), convertedObs.get("value"));
		Map encounterData = (Map) convertedObs.get("encounter");
		assertEquals(obs.getEncounter().getUuid(), encounterData.get("uuid"));
		assertEquals(obs.getEncounter().getEncounterType().getUuid(),
		    encounterData.get(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE));
		assertEquals(obs.getEncounter().getForm().getUuid(), encounterData.get("form"));
		assertEquals(formNamespace, convertedObs.get("formFieldNamespace"));
		assertEquals(formFieldPath, convertedObs.get("formFieldPath"));
	}
	
	@Test
	public void shouldCovertAnEncounterLessObs() {
		Obs obs = Context.getObsService().getObs(1002);
		obs.setEncounter(null);
		Map convertedObs = (Map) converter.convert(obs);
		assertEquals(obs.getUuid(), convertedObs.get("uuid"));
		assertEquals(obs.getConcept().getUuid(), convertedObs.get(PatientGridConstants.PROP_CONCEPT));
		assertEquals(Double.valueOf(82), convertedObs.get("value"));
		assertNull(convertedObs.get("encounter"));
	}
	
	@Test
	public void shouldCovertAnFormLessObs() {
		Obs obs = Context.getObsService().getObs(1002);
		Map convertedObs = (Map) converter.convert(obs);
		assertEquals(obs.getUuid(), convertedObs.get("uuid"));
		assertEquals(obs.getConcept().getUuid(), convertedObs.get(PatientGridConstants.PROP_CONCEPT));
		assertEquals(Double.valueOf(82), convertedObs.get("value"));
		Map encounterData = (Map) convertedObs.get("encounter");
		assertEquals(obs.getEncounter().getUuid(), encounterData.get("uuid"));
		assertEquals(obs.getEncounter().getEncounterType().getUuid(),
		    encounterData.get(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE));
		assertNull(encounterData.get("form"));
	}
	
	@Test
	public void shouldIncludeTheAnswerConceptUuidForAnObsWithACodedValue() {
		Obs obs = Context.getObsService().getObs(1008);
		Map convertedObs = (Map) converter.convert(obs);
		assertEquals(obs.getUuid(), convertedObs.get("uuid"));
		assertEquals(obs.getConcept().getUuid(), convertedObs.get(PatientGridConstants.PROP_CONCEPT));
		Map codedValue = (Map) convertedObs.get("value");
		assertEquals(obs.getValueCoded().getUuid(), codedValue.get("uuid"));
		assertEquals(obs.getValueCoded().getDisplayString(), codedValue.get(PatientGridConstants.PROPERTY_DISPLAY));
	}
	
}
