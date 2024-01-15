package org.openmrs.module.patientgrid.evaluator;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.definition.DateForLatestEncounterPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class DateForLatestEncounterPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

  @Autowired
  private PatientDataService patientDataService;

  @Autowired
  private EncounterService encounterService;

  @Before
  public void setup() {
    executeDataSet("entityBasisMaps.xml");
    executeDataSet("patientGrids.xml");
    executeDataSet("patientGridsTestData.xml");
  }

  @Test
  public void evaluate_shouldReturnTheDatesForTheMostRecentEncounters() throws Exception {
    final Integer patientId2 = 2;
    final Integer patientId6 = 6;
    final Integer patientId7 = 7;
    final Integer patientId999 = 999;//Has no encounter
    EvaluationContext context = new EvaluationContextPersistantCache();
    context.setBaseCohort(new Cohort(asList(patientId2, patientId6, patientId7, patientId999)));

    DateForLatestEncounterPatientDataDefinition def = new DateForLatestEncounterPatientDataDefinition();
    def.setEncounterType(new EncounterType(101));
    EvaluatedPatientData data = patientDataService.evaluate(def, context);

    assertEquals(3, data.getData().size());
    assertEquals(encounterService.getEncounter(2004).getEncounterDatetime(), data.getData().get(patientId2));
    assertEquals(encounterService.getEncounter(2006).getEncounterDatetime(), data.getData().get(patientId6));
    assertEquals(encounterService.getEncounter(2007).getEncounterDatetime(), data.getData().get(patientId7));
    assertNull(data.getData().get(patientId999));
  }

}
