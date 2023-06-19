package org.openmrs.module.patientgrid.function;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.period.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class MostRecentEncounterIdByTypeFunction implements Function<EncounterType, List> {
	
	private static final Logger log = LoggerFactory.getLogger(MostRecentEncounterIdByTypeFunction.class);
	
	private DateRange periodRange;
	
	private SessionFactory sf;
	
	public MostRecentEncounterIdByTypeFunction(SessionFactory sf, DateRange periodRange) {
		this.sf = sf;
		this.periodRange = periodRange;
	}
	
	@Override
	public List apply(EncounterType encounterType) {
		log.debug("Loading ids for the latest patient encounters of type: {}", encounterType);
		Query query = sf.getCurrentSession().createSQLQuery(getQuery(periodRange));
		query.setParameter("encType", encounterType);
		if (periodRange != null) {
			query.setParameter("fromDate", periodRange.getFromInServerTz());
			query.setParameter("toDate", periodRange.getToInServerTz());
		}
		return query.list();
	}
	
	private String getQuery(DateRange periodRange) {
		final String MOST_RECENT_DATES = "SELECT e.patient_id, max(e.encounter_datetime) AS maxDate FROM encounter e, "
		        + "patient p WHERE e.patient_id = p.patient_id AND e.encounter_type = :encType AND e.voided = false AND"
		        + " p.voided = false GROUP BY e.patient_id";
		
		final String MOST_RECENT_DATES_WITHIN_PERIODRANGE = "SELECT e.patient_id, max(e.encounter_datetime) AS maxDate FROM encounter e, "
		        + "patient p WHERE e.patient_id = p.patient_id AND e.encounter_type = :encType AND e.voided = false AND"
		        + " p.voided = false AND (e.encounter_datetime BETWEEN :fromDate AND :toDate) GROUP BY e.patient_id";
		
		if (periodRange != null) {
			return "SELECT e1.encounter_id FROM encounter e1 INNER JOIN (" + MOST_RECENT_DATES_WITHIN_PERIODRANGE
			        + ") e2 ON e1.patient_id = e2.patient_id AND e1.encounter_datetime = e2.maxDate";
		}
		return "SELECT e1.encounter_id FROM encounter e1 INNER JOIN (" + MOST_RECENT_DATES
		        + ") e2 ON e1.patient_id = e2.patient_id AND e1.encounter_datetime = e2.maxDate";
	}
}
