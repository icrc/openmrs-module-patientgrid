package org.openmrs.module.patientgrid.function;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.EncounterType;
import org.openmrs.module.patientgrid.PeriodRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class MostRecentEncounterIdByTypeFunction implements Function<EncounterType, List> {
	
	private final static String MOST_RECENT_DATES = "SELECT e.patient_id, max(e.encounter_datetime) AS maxDate FROM encounter e, "
	        + "patient p WHERE e.patient_id = p.patient_id AND e.encounter_type = :encType AND e.voided = false AND"
	        + " p.voided = false GROUP BY e.patient_id";
	
	private final static String MOST_RECENT_ENC_IDS = "SELECT e1.encounter_id FROM encounter e1 INNER JOIN ("
	        + MOST_RECENT_DATES + ") e2 ON e1.patient_id = e2.patient_id AND e1.encounter_datetime = e2.maxDate";
	
	private final PeriodRange periodRange;
	
	private SessionFactory sf;
	
	private static final Logger log = LoggerFactory.getLogger(MostRecentEncounterIdByTypeFunction.class);
	
	public MostRecentEncounterIdByTypeFunction(SessionFactory sf, PeriodRange periodRange) {
		this.sf = sf;
		this.periodRange = periodRange;
	}
	
	@Override
	public List apply(EncounterType encounterType) {
		log.debug("Loading ids for the latest patient encounters of type: {}", encounterType);
		Query query = sf.getCurrentSession().createSQLQuery(MOST_RECENT_ENC_IDS);
		query.setParameter("encType", encounterType);
		return query.list();
	}
}
