package org.openmrs.module.patientgrid;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EvaluationContextPersistantCache extends EvaluationContext {
	
	/**
	 * Must be initialize at startup to be shared with child context.
	 */
	private transient Map<String, Object> persistentCache = new HashMap<>();
	
	public EvaluationContextPersistantCache() {
	}
	
	public EvaluationContextPersistantCache(EvaluationContextPersistantCache evaluationContextPersistantCache) {
		super(evaluationContextPersistantCache);
		persistentCache = evaluationContextPersistantCache.persistentCache;
	}
	
	public Object computeIfAbsent(EncounterType type, Function<EncounterType, ?> function) {
		String key = function.getClass().getName() + "_" + type.getName();
		Object res = getFromPersistentCache(key);
		if (res == null) {
			res = function.apply(type);
			addToPersistentCache(key, res);
		}
		return res;
		
	}
	
	public Map computeMapIfAbsent(EncounterType type, Function<EncounterType, Map> function) {
		return (Map) computeIfAbsent(type, function);
		
	}
	
	/**
	 * @param functionClass
	 * @return all cached data for this function cache.
	 */
	public List<Map> getAllCacheData(Class functionClass) {
		List res = new ArrayList();
		String prefix = functionClass.getName() + "_";
		getPersistentCache().entrySet().forEach(entry -> {
			if (entry.getKey().startsWith(prefix)) {
				res.add(entry.getValue());
			}
		});
		return res;
	}
	
	public List computeListIfAbsent(EncounterType type, Function<EncounterType, List> function) {
		return (List) computeIfAbsent(type, function);
	}
	
	@JsonIgnore
	private Map<String, Object> getPersistentCache() {
		return persistentCache;
	}
	
	public Object getFromPersistentCache(String key) {
		return getPersistentCache().get(key);
	}
	
	public void addToPersistentCache(String key, Object value) {
		getPersistentCache().put(key, value);
	}
	
	@Override
	public EvaluationContext shallowCopy() {
		return new EvaluationContextPersistantCache(this);
	}
	
	public void clearPersistentCache() {
		persistentCache = null;
	}
	
	public void saveLatestEncDate(Integer patientId, Encounter value) {
		HashMap<Integer, Date> patientDate = getPatientNewestDate();
		Date currentDate = patientDate.get(patientId);
		if (currentDate == null || value.getEncounterDatetime().after(currentDate)) {
			patientDate.put(patientId, value.getEncounterDatetime());
		}
	}
	
	private HashMap<Integer, Date> getPatientNewestDate() {
		return (HashMap<Integer, Date>) persistentCache.computeIfAbsent("patientDate", s -> new HashMap<Integer, Date>());
	}
	
	Date getLatestEncounterDate(Integer patientId) {
		return getPatientNewestDate().get(patientId);
	}
	
	/**
	 * @param limit the max number of rows. if -1 no limit
	 */
	public void limitAndSortCohortBasedOnEncounterDate(int limit) {
		Cohort baseCohort = getBaseCohort();
		HashMap<Integer, Date> patientNewestDate = getPatientNewestDate();
		Cohort cohort = new Cohort();
		for (CohortMembership member : baseCohort.getMemberships()) {
			cohort.addMembership(new CohortMembership(member.getPatientId(), patientNewestDate.get(member.getPatientId())));
		}
		if (limit > 0 && limit < cohort.size()) {
			List<CohortMembership> collect = cohort.getMemberships().stream().limit(limit).collect(Collectors.toList());
			cohort = new Cohort();
			cohort.setMemberships(collect);
		}
		setBaseCohort(cohort);
	}
	
}
