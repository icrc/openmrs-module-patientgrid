package org.openmrs.module.patientgrid;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.openmrs.module.patientgrid.PatientGridConstants.GP_AGE_RANGES;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.reporting.common.Age.Unit;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.AgeRangeConverter;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class PatientGridUtilsTest {
	
	@Mock
	private AdministrationService mockAdminService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private DataDefinition getDefinition(String columnName, PatientDataSetDefinition def) {
		JoinDataDefinition nameDef = (JoinDataDefinition) ((Mapped) def.getColumnDefinition(columnName).getDataDefinition())
		        .getParameterizable();
		return nameDef.getJoinedDefinition();
	}
	
	@Test
	public void convertToReportDefinition_shouldConvertThePatientGridToAReportDefinition() {
		final String gender = "gender";
		final String name = "name";
		final String ageAtEnc = "ageAtEnc";
		final String ageCategory = "ageCategory";
		final String admissionType = "admissionType";
		final EncounterType initial = new EncounterType();
		final EncounterType admission = new EncounterType();
		final Concept admissionConcept = new Concept();
		final String structure = "structure";
		final String country = "country";
		PatientGrid patientGrid = new PatientGrid();
		patientGrid.addColumn(new PatientGridColumn(name, ColumnDatatype.NAME));
		patientGrid.addColumn(new PatientGridColumn(gender, ColumnDatatype.GENDER));
		patientGrid.addColumn(new AgeAtEncounterPatientGridColumn(ageAtEnc, initial));
		patientGrid.addColumn(new AgeAtEncounterPatientGridColumn(ageCategory, admission, true));
		patientGrid.addColumn(new PatientGridColumn(structure, ColumnDatatype.DATAFILTER_LOCATION));
		patientGrid.addColumn(new PatientGridColumn(country, ColumnDatatype.DATAFILTER_COUNTRY));
		patientGrid.addColumn(new ObsPatientGridColumn(admissionType, admissionConcept, admission));
		PowerMockito.mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		when(mockAdminService.getGlobalProperty(PatientGridConstants.GP_AGE_RANGES)).thenReturn("0-17:<18yrs,18+");
		
		ReportDefinition reportDef = PatientGridUtils.convertToReportDefinition(patientGrid);
		
		PatientDataSetDefinition patientData = (PatientDataSetDefinition) ((Mapped) reportDef.getDataSetDefinitions()
		        .get("patientData")).getParameterizable();
		assertEquals(PreferredNameDataDefinition.class, getDefinition(name, patientData).getClass());
		assertEquals(GenderDataDefinition.class, getDefinition(gender, patientData).getClass());
		MappedData ageDef = patientData.getColumnDefinition(ageAtEnc).getDataDefinition();
		assertEquals(PatientAgeAtEncounterDataDefinition.class, ageDef.getParameterizable().getClass());
		assertNull(ageDef.getConverters());
		Parameterizable locationDef = ((Mapped) patientData.getColumnDefinition(structure).getDataDefinition())
		        .getParameterizable();
		assertEquals(PatientLocationDataDefinition.class, locationDef.getClass());
		Parameterizable countryDef = ((Mapped) patientData.getColumnDefinition(country).getDataDefinition())
		        .getParameterizable();
		assertEquals(PatientCountryDataDefinition.class, countryDef.getClass());
		MappedData ageCategoryDef = patientData.getColumnDefinition(ageCategory).getDataDefinition();
		assertEquals(PatientAgeAtEncounterDataDefinition.class, ageCategoryDef.getParameterizable().getClass());
		assertEquals(1, ageCategoryDef.getConverters().size());
		List<AgeRange> ageRanges = ((AgeRangeConverter) ageCategoryDef.getConverters().get(0)).getAgeRanges();
		assertEquals(2, ageRanges.size());
		AgeRange ageRange = ageRanges.get(0);
		assertEquals(0, ageRange.getMinAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMinAgeUnit());
		assertEquals(17, ageRange.getMaxAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMaxAgeUnit());
		assertEquals("<18yrs", ageRange.getLabel());
		
		ageRange = ageRanges.get(1);
		assertEquals(18, ageRange.getMinAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMinAgeUnit());
		assertNull(ageRange.getMaxAge());
		assertNull(ageRange.getMaxAgeUnit());
		assertEquals("18+", ageRange.getLabel());
		ObsForMostRecentEncounterDataDefinition obsDef = (ObsForMostRecentEncounterDataDefinition) ((Mapped) patientData
		        .getColumnDefinition(admissionType).getDataDefinition()).getParameterizable();
		assertEquals(admissionConcept, obsDef.getConcept());
		assertEquals(admission, obsDef.getEncounterType());
	}
	
	@Test
	public void getObsByConcept_shouldReturnNullIfNoMatchIsFound() {
		Encounter encounter = new Encounter();
		Obs obs = new Obs();
		obs.setConcept(new Concept());
		encounter.addObs(obs);
		assertNull(PatientGridUtils.getObsByConcept(encounter, new Concept()));
	}
	
	@Test
	public void getObsByConcept_shouldExcludeObsGroupings() {
		final String conceptUuid = "test-uuid";
		Encounter encounter = new Encounter();
		Concept obsConcept = new Concept();
		obsConcept.setUuid(conceptUuid);
		Obs obs = new Obs();
		obs.setConcept(obsConcept);
		obs.addGroupMember(new Obs());
		encounter.addObs(obs);
		Concept concept = new Concept();
		concept.setUuid(conceptUuid);
		assertNull(PatientGridUtils.getObsByConcept(encounter, concept));
	}
	
	@Test
	public void getObsByConcept_shouldExcludeObsGroupingsEvenIfTheMembersAreVoided() {
		final String conceptUuid = "test-uuid";
		Encounter encounter = new Encounter();
		Concept obsConcept = new Concept();
		obsConcept.setUuid(conceptUuid);
		Obs obs = new Obs();
		obs.setConcept(obsConcept);
		Obs member = new Obs();
		member.setVoided(true);
		obs.addGroupMember(member);
		encounter.addObs(obs);
		Concept concept = new Concept();
		concept.setUuid(conceptUuid);
		assertNull(PatientGridUtils.getObsByConcept(encounter, concept));
	}
	
	@Test
	public void getObsByConcept_shouldExcludeVoidedObs() {
		final String conceptUuid = "test-uuid";
		Encounter encounter = new Encounter();
		Concept obsConcept = new Concept();
		obsConcept.setUuid(conceptUuid);
		Obs obs = new Obs();
		obs.setConcept(obsConcept);
		obs.setVoided(true);
		encounter.addObs(obs);
		Concept concept = new Concept();
		concept.setUuid(conceptUuid);
		assertNull(PatientGridUtils.getObsByConcept(encounter, concept));
	}
	
	@Test
	public void getObsByConcept_shouldReturnTheObsWithAQuestionMatchingTheSpecifiedConcept() {
		final String conceptUuid = "test-uuid";
		Encounter encounter = new Encounter();
		Concept obsConcept = new Concept();
		obsConcept.setUuid(conceptUuid);
		Obs obs1 = new Obs();
		obs1.setConcept(new Concept());
		Obs obs2 = new Obs();
		obs2.setConcept(obsConcept);
		encounter.addObs(obs1);
		encounter.addObs(obs2);
		Concept concept = new Concept();
		concept.setUuid(conceptUuid);
		assertEquals(obs2, PatientGridUtils.getObsByConcept(encounter, concept));
	}
	
	@Test
	public void getObsByConcept_shouldFailIfMultipleMatchesAreFound() {
		final String conceptUuid = "test-uuid";
		Encounter encounter = new Encounter();
		Concept obsConcept = new Concept();
		obsConcept.setUuid(conceptUuid);
		Obs obs1 = new Obs();
		obs1.setConcept(new Concept());
		Obs obs2 = new Obs();
		obs2.setConcept(obsConcept);
		Obs obs3 = new Obs();
		obs3.setConcept(obsConcept);
		encounter.addObs(obs1);
		encounter.addObs(obs2);
		encounter.addObs(obs3);
		Concept concept = new Concept();
		concept.setUuid(conceptUuid);
		expectedException.expect(APIException.class);
		expectedException.expectMessage(
		    equalTo("Found multiple obs with question concept " + concept + " for encounter " + encounter));
		PatientGridUtils.getObsByConcept(encounter, concept);
	}
	
	@Test
	public void parseAgeRangeString_shouldGenerateAListOfAgeRangesByParsingTheSpecifiedString() {
		final String str = "0-18:<18,19-29:Youth,30-39, 40-54:Middle Aged,55+";
		List<AgeRange> ageRanges = PatientGridUtils.parseAgeRangeString(str);
		assertEquals(5, ageRanges.size());
		AgeRange ageRange = ageRanges.get(0);
		assertEquals(0, ageRange.getMinAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMinAgeUnit());
		assertEquals(18, ageRange.getMaxAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMaxAgeUnit());
		assertEquals("<18", ageRange.getLabel());
		
		ageRange = ageRanges.get(1);
		assertEquals(19, ageRange.getMinAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMinAgeUnit());
		assertEquals(29, ageRange.getMaxAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMaxAgeUnit());
		assertEquals("Youth", ageRange.getLabel());
		
		ageRange = ageRanges.get(2);
		assertEquals(30, ageRange.getMinAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMinAgeUnit());
		assertEquals(39, ageRange.getMaxAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMaxAgeUnit());
		assertEquals("30-39", ageRange.getLabel());
		
		ageRange = ageRanges.get(3);
		assertEquals(40, ageRange.getMinAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMinAgeUnit());
		assertEquals(54, ageRange.getMaxAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMaxAgeUnit());
		assertEquals("Middle Aged", ageRange.getLabel());
		
		ageRange = ageRanges.get(4);
		assertEquals(55, ageRange.getMinAge().intValue());
		assertEquals(Unit.YEARS, ageRange.getMinAgeUnit());
		assertNull(ageRange.getMaxAge());
		assertNull(ageRange.getMaxAgeUnit());
		assertEquals("55+", ageRange.getLabel());
	}
	
	@Test
	public void convertToReportDefinition_shouldFailIfTheGlobalPropertyValueForAgeRangesIsNotSet() {
		PatientGrid patientGrid = new PatientGrid();
		patientGrid.addColumn(new AgeAtEncounterPatientGridColumn("ageCategory", null, true));
		expectedException.expect(APIException.class);
		expectedException.expectMessage(
		    equalTo("No ranges defined, please set the value for the global property named: " + GP_AGE_RANGES));
		PowerMockito.mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		PatientGridUtils.convertToReportDefinition(patientGrid);
	}
	
	@Test
	public void convertToReportDefinition_shouldFailIfTheGlobalPropertyValueForAgeRangesIsBlank() {
		PatientGrid patientGrid = new PatientGrid();
		patientGrid.addColumn(new AgeAtEncounterPatientGridColumn("ageCategory", null, true));
		expectedException.expect(APIException.class);
		expectedException.expectMessage(
		    equalTo("No ranges defined, please set the value for the global property named: " + GP_AGE_RANGES));
		PowerMockito.mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		when(mockAdminService.getGlobalProperty(PatientGridConstants.GP_AGE_RANGES)).thenReturn("");
		PatientGridUtils.convertToReportDefinition(patientGrid);
	}
	
	@Test
	public void convertToReportDefinition_shouldFailIfTheGlobalPropertyValueForAgeRangesIsAnEmptyString() {
		PatientGrid patientGrid = new PatientGrid();
		patientGrid.addColumn(new AgeAtEncounterPatientGridColumn("ageCategory", null, true));
		expectedException.expect(APIException.class);
		expectedException.expectMessage(
		    equalTo("No ranges defined, please set the value for the global property named: " + GP_AGE_RANGES));
		PowerMockito.mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		when(mockAdminService.getGlobalProperty(PatientGridConstants.GP_AGE_RANGES)).thenReturn(" ");
		PatientGridUtils.convertToReportDefinition(patientGrid);
	}
	
}
