package org.openmrs.module.patientgrid.web.rest.v1_0.search;

import static org.openmrs.module.patientgrid.PatientGridConstants.MODULE_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.EvaluationContextPersistantCache;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

@Component(MODULE_ID + ".encounterHistorySearchHandler")
public class EncounterHistorySearchHandler implements SearchHandler {
	
	protected static final String PARAM_PATIENT = "patient";
	
	protected static final String PARAM_ENC_TYPE = "encounterType";
	
	protected static final String SEARCH_CONFIG_NAME = MODULE_ID + "GetEncounterHistory";
	
	private static final SearchQuery QUERY = new SearchQuery.Builder(
	        "Allows you to find encounters of a specified encounter type for a single patient")
	                .withRequiredParameters(PARAM_PATIENT, PARAM_ENC_TYPE).build();
	
	private static final SearchConfig CONFIG = new SearchConfig(SEARCH_CONFIG_NAME, RestConstants.VERSION_1 + "/encounter",
	        Arrays.asList("1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*", "2.5.*"), QUERY);
	
	@Override
	public SearchConfig getSearchConfig() {
		return CONFIG;
	}
	
	/**
	 * @see SearchHandler#search(RequestContext)
	 */
	@Override
	public PageableResult search(RequestContext requestContext) throws ResponseException {
		final String patientUuid = requestContext.getParameter(PARAM_PATIENT);
		final String encounterTypeUuid = requestContext.getParameter(PARAM_ENC_TYPE);
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		if (patient == null) {
			throw new ObjectNotFoundException();
		}
		
		EncounterType type = Context.getEncounterService().getEncounterTypeByUuid(encounterTypeUuid);
		if (type == null) {
			throw new ObjectNotFoundException();
		}
		
		Cohort cohort = new Cohort();
		Integer patientId = patient.getId();
		cohort.addMember(patientId);
		EvaluationContext context = new EvaluationContextPersistantCache();
		context.setBaseCohort(cohort);
		
		try {
			List<Encounter> encs = new ArrayList();
			Map<Integer, Object> idAndEncs = PatientGridUtils.getEncounters(type, context, false);
			if (!idAndEncs.isEmpty()) {
				encs = (List) idAndEncs.get(patientId);
			}
			
			return new NeedsPaging(encs, requestContext);
		}
		catch (Exception e) {
			throw new GenericRestException(e);
		}
	}
	
}
