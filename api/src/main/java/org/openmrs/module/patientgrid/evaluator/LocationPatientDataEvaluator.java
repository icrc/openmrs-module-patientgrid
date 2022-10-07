package org.openmrs.module.patientgrid.evaluator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.datafilter.impl.EntityBasisMap;
import org.openmrs.module.patientgrid.definition.LocationPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

@Handler(supports = LocationPatientDataDefinition.class, order = 50)
public class LocationPatientDataEvaluator implements PatientDataEvaluator {
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.entityIdentifier", "e.basisIdentifier", "e.dateCreated");
		q.from(EntityBasisMap.class, "e");
		q.whereEqual("e.basisType", Location.class.getName());
		q.whereEqual("e.entityType", Patient.class.getName());
		q.wherePatientIn("e.entityIdentifier", context);
		
		Map<Integer, Date> patientIdAndMapDateCreated = new HashMap();
		List<Object[]> rows = Context.getService(EvaluationService.class).evaluateToList(q, context);
		LocationService locationService = Context.getLocationService();
		Map<Integer, Object> patientIdAndLocationMap = new HashMap(rows.size());
		for (Object[] row : rows) {
			Integer patientId = Integer.valueOf(row[0].toString());
			Date mapDateCreated = (Date) row[2];
			//In case of multiple mapped locations, pick most recently added
			if (patientIdAndLocationMap.containsKey(patientId)
			        && patientIdAndMapDateCreated.get(patientId).after(mapDateCreated)) {
				
				continue;
			}
			
			patientIdAndLocationMap.put(patientId, locationService.getLocation(Integer.valueOf(row[1].toString())));
			patientIdAndMapDateCreated.put(patientId, mapDateCreated);
		}
		
		EvaluatedPatientData data = new EvaluatedPatientData(definition, context);
		data.setData(patientIdAndLocationMap);
		
		return data;
	}
	
}
