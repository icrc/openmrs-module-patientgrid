package org.openmrs.module.patientgrid.converter;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObsValueConverter;

public class PatientGridObsConverter implements DataConverter {

  private static final ObsValueConverter OBS_VALUE_CONVERTER = new ObsValueConverter();

  @Override
  public Object convert(Object original) {
    if (original != null) {
      Obs obs = (Obs) original;
      Map obsData = new HashMap(5);
      obsData.put("uuid", obs.getUuid());
      obsData.put(PatientGridConstants.PROP_CONCEPT, obs.getConcept().getUuid());
      Object value;
      if (((Obs) original).getConcept().getDatatype().isCoded()) {
        Map answerConcept = new HashMap(2);
        answerConcept.put("uuid", obs.getValueCoded().getUuid());
        answerConcept.put(PatientGridConstants.PROPERTY_DISPLAY, DataUtil.convertData(obs, OBS_VALUE_CONVERTER));
        value = answerConcept;
      } else {
        value = DataUtil.convertData(obs, OBS_VALUE_CONVERTER);
      }

      obsData.put("value", value);

      if (obs.getEncounter() != null) {
        Encounter encounter = obs.getEncounter();
        Map encData = new HashMap(3);
        encData.put("uuid", encounter.getUuid());
        encData.put(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE, encounter.getEncounterType().getUuid());
        if (encounter.getForm() != null) {
          encData.put("form", encounter.getForm().getUuid());
        }
        obsData.put("encounter", encData);
      }

      obsData.put("formFieldNamespace", obs.getFormFieldNamespace());
      obsData.put("formFieldPath", obs.getFormFieldPath());

      return obsData;
    }

    return null;
  }

  @Override
  public Class<?> getInputDataType() {
    return Obs.class;
  }

  @Override
  public Class<?> getDataType() {
    return Map.class;
  }

}
