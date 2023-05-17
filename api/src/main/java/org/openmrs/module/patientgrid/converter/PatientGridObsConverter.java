package org.openmrs.module.patientgrid.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.patientgrid.PatientGridConstants;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObsValueConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientGridObsConverter implements DataConverter {
	
	public static final String VALUE_DELIMITER = ",";
	
	private static final ObsValueConverter OBS_VALUE_CONVERTER = new ObsValueConverter();
	
	private static final Logger LOG = LoggerFactory.getLogger(PatientGridObsConverter.class);
	
	private Concept getConcept(Collection<Obs> obs) {
		Concept concept = null;
		Obs firstObs = null;
		for (Obs o : obs) {
			if (concept == null) {
				concept = o.getConcept();
				firstObs = o;
			} else if (!concept.getUuid().equals(o.getConcept().getUuid())) {
				LOG.error("can convert list of obs as obs {} and obs {} don't have the same concept {}", firstObs.getUuid(),
				    o.getUuid(), concept.getUuid());
				throw new IllegalAccessError("obs must be have the same concept");
			}
			
		}
		return concept;
	}
	
	private Encounter getEncounter(Collection<Obs> obs) {
		Encounter encounter = null;
		Obs firstObs = null;
		for (Obs o : obs) {
			if (encounter == null) {
				encounter = o.getEncounter();
				firstObs = o;
			} else if (!encounter.getUuid().equals(o.getEncounter().getUuid())) {
				LOG.error("can convert list of obs as obs {} and obs {} don't have the same concept {}", firstObs.getUuid(),
				    o.getUuid(), encounter.getUuid());
				throw new IllegalAccessError("obs must be have the same encounter");
			}
			
		}
		return encounter;
	}
	
	private String getFormFieldNamespace(Collection<Obs> obs) {
		String formFieldNamespace = null;
		Obs firstObs = null;
		for (final Obs o : obs) {
			if (formFieldNamespace == null) {
				formFieldNamespace = o.getFormFieldNamespace();
				firstObs = o;
			} else if (!formFieldNamespace.equals(o.getFormFieldNamespace())) {
				LOG.error("can convert list of obs as obs {} and obs {} don't have the same FormNamespaceAndPath {}",
				    firstObs.getUuid(), o.getUuid(), formFieldNamespace);
				throw new IllegalAccessError("obs must be have the same getFormFieldNamespace");
			}

		}
		return formFieldNamespace;
	}

	private String getFormFieldPath(Collection<Obs> obs) {
		String formFieldPath = null;
		Obs firstObs = null;
		for (final Obs o : obs) {
			if (formFieldPath == null) {
				formFieldPath = o.getFormFieldPath();
				firstObs = o;
			} else if (!formFieldPath.equals(o.getFormFieldPath())) {
				LOG.error("can convert list of obs as obs {} and obs {} don't have the same FormFieldPath {}",
				    firstObs.getUuid(), o.getUuid(), formFieldPath);
//				throw new IllegalAccessError("obs must be have the same FormFieldPath");
			}

		}
		return formFieldPath;
	}

	public Object convertOne(final Obs original) {
		return convert(Collections.singleton(original));
	}

	@Override
	public Object convert(final Object original) {
		if (CollectionUtils.isNotEmpty((Collection) original)) {
			final Collection<Obs> obsList = (Collection<Obs>) original;
			final Concept concept = getConcept(obsList);
			if (concept == null) {
				throw new IllegalAccessError("obs must be have a concept");
			}
			final Map obsData = new HashMap(5);
			obsData.put("uuid", obsList.stream().map(Obs::getUuid).collect(Collectors.joining(VALUE_DELIMITER)));
			obsData.put(PatientGridConstants.PROP_CONCEPT, concept.getUuid());
			Object value = null;
			if (obsList.size() == 1) {
				value = DataUtil.convertData(obsList.iterator().next(), OBS_VALUE_CONVERTER);
			} else {
				value = obsList.stream().map(o -> DataUtil.convertData(o, OBS_VALUE_CONVERTER).toString()).sorted()
				        .collect(Collectors.joining(VALUE_DELIMITER));
			}
			if (concept.getDatatype().isCoded()) {
				Map answerConcept = new HashMap(2);
				answerConcept.put("uuid",
				    obsList.stream().map(o -> o.getValueCoded().getUuid()).collect(Collectors.joining(VALUE_DELIMITER)));
				answerConcept.put(PatientGridConstants.PROPERTY_DISPLAY, value);
				value = answerConcept;
			}

			obsData.put("value", value);
			Encounter encounter = getEncounter(obsList);
			if (encounter != null) {
				Map encData = new HashMap(3);
				encData.put("uuid", encounter.getUuid());
				encData.put(PatientGridConstants.PROPERTY_ENCOUNTER_TYPE, encounter.getEncounterType().getUuid());
				if (encounter.getForm() != null) {
					encData.put("form", encounter.getForm().getUuid());
				}
				obsData.put("encounter", encData);
			}

			obsData.put("formFieldNamespace", getFormFieldNamespace(obsList));
			obsData.put("formFieldPath", getFormFieldPath(obsList));

			return obsData;
		}
		
		return null;
	}
	
	@Override
	public Class<?> getInputDataType() {
		return Collection.class;
	}
	
	@Override
	public Class<?> getDataType() {
		return Map.class;
	}
	
}
