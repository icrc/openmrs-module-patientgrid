package org.openmrs.module.patientgrid;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EncounterComparator implements Comparator<Encounter> {

  @Override
  public int compare(Encounter o1, Encounter o2) {
    if (o1 == o2) {
      return 0;
    }
    if (o1 == null) {
      return -1;
    }
    if (o2 == null) {
      return 1;
    }
    if (StringUtils.equals(o1.getUuid(), o2.getUuid())) {
      return 0;
    }
    return o1.getEncounterDatetime().compareTo(o2.getEncounterDatetime());
  }

  public static void sortListOfEncounters(Map<Integer, Object> encounterLists) {
    Comparator<Encounter> comparator = new EncounterComparator().reversed();
    encounterLists.values().forEach(o -> {
      if (o instanceof List) {
        ((List) o).sort(comparator);
      }
    });
  }
}
