package org.openmrs.module.patientgrid;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.Obs;
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
			obsData.put("concept", obs.getConcept().getUuid());
			obsData.put("value", DataUtil.convertData(obs, OBS_VALUE_CONVERTER));
			if (obs.getEncounter() != null) {
				Encounter encounter = obs.getEncounter();
				Map encData = new HashMap(3);
				encData.put("uuid", encounter.getUuid());
				encData.put("encounterType", encounter.getEncounterType().getUuid());
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
