package org.openmrs.module.patientgrid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SafePowerMockRunner.class)
@PrepareForTest(Context.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class EvaluationContextPersistantCacheTest {

  private static EvaluationContextPersistantCache createContext() throws ParseException {
    EvaluationContextPersistantCache evaluationContextPersistantCache = new EvaluationContextPersistantCache();
    Encounter e3 = new Encounter();
    e3.setEncounterDatetime(PatientGridConstants.DATE_FORMAT.parse("2023-01-03"));
    Encounter e4 = new Encounter();
    e4.setEncounterDatetime(PatientGridConstants.DATE_FORMAT.parse("2023-01-03"));
    evaluationContextPersistantCache.saveLatestEncDate(5, createEncounter("2023-01-03"));
    evaluationContextPersistantCache.saveLatestEncDate(5, createEncounter("2023-01-04"));
    evaluationContextPersistantCache.saveLatestEncDate(4, createEncounter("2023-01-02"));
    evaluationContextPersistantCache.saveLatestEncDate(3, createEncounter("2023-01-01"));
    evaluationContextPersistantCache.saveLatestEncDate(2, createEncounter("2023-01-01"));

    List<Integer> patientsOrIds = Arrays.asList(5, 4, 3, 2, 1);
    evaluationContextPersistantCache.setBaseCohort(new Cohort(patientsOrIds));
    return evaluationContextPersistantCache;
  }

  private static Encounter createEncounter(String date) throws ParseException {
    Encounter e1 = new Encounter();
    e1.setEncounterDatetime(PatientGridConstants.DATE_FORMAT.parse(date));
    return e1;
  }

  @Before
  public void setup() {
    PowerMockito.mockStatic(Context.class);
    when(Context.getAuthenticatedUser()).thenReturn(new User());
  }

  @Test
  public void initContext_shouldSaveLatestEncounterDate() throws ParseException {
    //setup
    EvaluationContextPersistantCache evaluationContextPersistantCache = createContext();
    List<CohortMembership> members = new ArrayList<>(evaluationContextPersistantCache.getBaseCohort().getMemberships());
    Assert.assertEquals(1, members.get(0).getPatientId().intValue());
    Assert.assertEquals(2, members.get(1).getPatientId().intValue());
    Assert.assertEquals(3, members.get(2).getPatientId().intValue());
    Assert.assertEquals(4, members.get(3).getPatientId().intValue());
    Assert.assertEquals(5, members.get(4).getPatientId().intValue());
    Assert.assertEquals(PatientGridConstants.DATE_FORMAT.parse("2023-01-04"),
        evaluationContextPersistantCache.getLatestEncounterDate(5));
  }

  @Test
  public void limitAndSortCohortBasedOnEncounterDate_shouldSortPatient() throws ParseException {
    //setup
    EvaluationContextPersistantCache evaluationContextPersistantCache = createContext();

    //action
    evaluationContextPersistantCache.limitAndSortCohortBasedOnEncounterDate(-1);

    //assert
    ArrayList<CohortMembership> members = new ArrayList<>(
        evaluationContextPersistantCache.getBaseCohort().getMemberships());
    Assert.assertEquals(5, members.size());
    Assert.assertEquals(5, members.get(0).getPatientId().intValue());
    Assert.assertEquals(4, members.get(1).getPatientId().intValue());
    //patient sorted by patient id:
    Assert.assertEquals(2, members.get(2).getPatientId().intValue());
    Assert.assertEquals(3, members.get(3).getPatientId().intValue());
    //1 has no date
    Assert.assertEquals(1, members.get(4).getPatientId().intValue());

  }

  @Test
  public void limitAndSortCohortBasedOnEncounterDate_shouldLimitAndSortPatientTo3() throws ParseException {
    //setup
    EvaluationContextPersistantCache evaluationContextPersistantCache = createContext();

    //action
    evaluationContextPersistantCache.limitAndSortCohortBasedOnEncounterDate(3);

    //assert
    ArrayList<CohortMembership> members = new ArrayList<>(
        evaluationContextPersistantCache.getBaseCohort().getMemberships());
    Assert.assertEquals(3, members.size());
    Assert.assertEquals(5, members.get(0).getPatientId().intValue());
    Assert.assertEquals(4, members.get(1).getPatientId().intValue());
    //patient sorted by patient id:
    Assert.assertEquals(2, members.get(2).getPatientId().intValue());
  }
}
