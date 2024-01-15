package org.openmrs.module.patientgrid.evaluator;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.datafilter.impl.EntityBasisMap;
import org.openmrs.module.datafilter.impl.api.DataFilterService;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.definition.LocationDataFilterPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

  @Autowired
  private PatientDataService patientDataService;

  @Autowired
  private LocationService locationService;

  @Autowired
  private PatientService patientService;

  @Autowired
  private DataFilterService dataFilterService;

  @Test
  public void evaluate_shouldReturnThePatientLocation() throws Exception {
    executeDataSet("entityBasisMaps.xml");
    final Integer patientId2 = 2;
    final Integer patientId6 = 6;
    EvaluationContext context = new EvaluationContextPersistantCache();
    context.setBaseCohort(new Cohort(asList(patientId2, patientId6)));

    EvaluatedPatientData data = patientDataService.evaluate(new LocationDataFilterPatientDataDefinition(), context);

    assertEquals(2, data.getData().size());
    assertEquals(locationService.getLocation(4000), data.getData().get(patientId2));
    assertEquals(locationService.getLocation(4001), data.getData().get(patientId6));
  }

  @Test
  public void evaluate_shouldReturnNullForAPatientWithNoMappedLocation() throws Exception {
    executeDataSet("entityBasisMaps.xml");
    final Integer patientId = 999;
    Patient patient = patientService.getPatient(patientId);
    assertNotNull(patient);
    assertTrue(dataFilterService.getEntityBasisMaps(patient, Location.class.getName()).isEmpty());
    EvaluationContext context = new EvaluationContextPersistantCache();
    context.setBaseCohort(new Cohort(asList(patientId)));
    assertTrue(patientDataService.evaluate(new LocationDataFilterPatientDataDefinition(), context).getData().isEmpty());
  }

  @Test
  public void evaluate_shouldReturnTheLocationAssociatedToTheMostRecentMappingInCaseOfMoreThanOne() throws Exception {
    executeDataSet("entityBasisMaps.xml");
    final Integer patientId = 7;
    Patient patient = patientService.getPatient(patientId);
    List<EntityBasisMap> maps = (List) dataFilterService.getEntityBasisMaps(patient, Location.class.getName());
    assertEquals(3, maps.size());
    assertTrue(maps.get(0).getDateCreated().before(maps.get(1).getDateCreated()));
    EvaluationContext context = new EvaluationContextPersistantCache();
    context.setBaseCohort(new Cohort(asList(patientId)));
    LocationDataFilterPatientDataDefinition d = new LocationDataFilterPatientDataDefinition();

    EvaluatedPatientData data = patientDataService.evaluate(d, context);

    assertEquals(1, data.getData().size());
    assertEquals(locationService.getLocation(4001), data.getData().get(patientId));
  }

  @Test
  public void evaluate_shouldReturnTheLocationAssociatedToTheLastAddedMappingInCaseOfMoreThanOneWithSameDateCreated()
      throws Exception {
    executeDataSet("entityBasisMaps.xml");
    final Integer patientId = 432;
    Patient patient = patientService.getPatient(patientId);
    List<EntityBasisMap> maps = (List) dataFilterService.getEntityBasisMaps(patient, Location.class.getName());
    assertEquals(2, maps.size());
    assertEquals(maps.get(0).getDateCreated(), (maps.get(1).getDateCreated()));
    EvaluationContext context = new EvaluationContextPersistantCache();
    context.setBaseCohort(new Cohort(asList(patientId)));

    EvaluatedPatientData data = patientDataService.evaluate(new LocationDataFilterPatientDataDefinition(), context);

    assertEquals(1, data.getData().size());
    assertEquals(locationService.getLocation(4002), data.getData().get(patientId));
  }

}
