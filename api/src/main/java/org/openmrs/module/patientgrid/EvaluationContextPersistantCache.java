package org.openmrs.module.patientgrid;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
	
	public EvaluationContext shallowCopy() {
		return new EvaluationContextPersistantCache(this);
	}
	
	public void clearPersistentCache() {
		persistentCache = null;
	}
}
