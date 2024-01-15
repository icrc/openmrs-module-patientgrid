package org.openmrs.module.patientgrid.web.rest.v1_0.resource;

import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.NAMESPACE;
import static org.openmrs.module.patientgrid.web.rest.v1_0.PatientGridRestConstants.SUPPORTED_VERSIONS;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.*;
import org.openmrs.module.patientgrid.PatientGridColumn.ColumnDatatype;
import org.openmrs.module.patientgrid.api.PatientGridService;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.springframework.context.NoSuchMessageException;

@SubResource(parent = PatientGridResource.class, path = PatientGridConstants.PROPERTY_COLUMN, supportedClass = PatientGridColumn.class, supportedOpenmrsVersions = {
    SUPPORTED_VERSIONS})
public class PatientGridColumnResource extends DelegatingSubResource<PatientGridColumn, PatientGrid, PatientGridResource> {

  /**
   * @see DelegatingSubResource#hasTypesDefined()
   */
  @Override
  public boolean hasTypesDefined() {
    return true;
  }

  /**
   * @see DelegatingSubResource#getResourceName() ()
   */
  @Override
  protected String getResourceName() {
    return NAMESPACE + "/patientgrid/column";
  }

  /**
   * @see DelegatingSubResource#getRepresentationDescription(Representation)
   */
  @Override
  public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
    if (representation instanceof DefaultRepresentation || representation instanceof FullRepresentation) {
      DelegatingResourceDescription description = new DelegatingResourceDescription();
      description.addProperty("uuid");
      description.addProperty(PatientGridConstants.PROPERTY_DISPLAY);
      description.addRequiredProperty("name");
      description.addProperty(PatientGridConstants.PROP_DESCRIPTION);
      description.addRequiredProperty(PatientGridConstants.PROP_DATATYPE);
      description.addProperty(PatientGridConstants.PROP_HIDDEN);
      description.addProperty(PatientGridConstants.PROP_FILTERS);
      description.addSelfLink();
      if (representation instanceof DefaultRepresentation) {
        description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
      } else {
        description.addProperty("auditInfo");
      }

      return description;
    }

    // TODO remove this code when we add support to add columns to existing grids
    // Ref Rep, we don't delete to superclass because the call to getResource() fails
    DelegatingResourceDescription rep = new DelegatingResourceDescription();
    rep.addProperty("uuid");
    rep.addProperty(PatientGridConstants.PROPERTY_DISPLAY);
    rep.addSelfLink();

    return rep;
  }

  @PropertySetter(PatientGridConstants.PROP_FILTERS)
  public void setFilters(PatientGridColumn column, PatientGridColumnFilter... filters) {
    column.getFilters().clear();
    for (PatientGridColumnFilter filter : filters) {
      column.addFilter(filter);
    }
  }

  /**
   * @see DelegatingSubResource#newDelegate()
   */
  @Override
  public PatientGridColumn newDelegate() {
    return new PatientGridColumn();
  }

  @Override
  public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
    DelegatingResourceDescription description = new DelegatingResourceDescription();
    description.addRequiredProperty("name");
    description.addRequiredProperty(PatientGridConstants.PROP_DATATYPE);
    description.addProperty(PatientGridConstants.PROP_DESCRIPTION);
    description.addProperty(PatientGridConstants.PROP_FILTERS);
    description.addProperty(PatientGridConstants.PROP_HIDDEN);
    return description;
  }

  /**
   * @see DelegatingSubResource#getUpdatableProperties()
   */
  @Override
  public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
    DelegatingResourceDescription description = super.getUpdatableProperties();
    description.removeProperty(PatientGridConstants.PROP_DATATYPE);
    return description;
  }

  @PropertyGetter(PatientGridConstants.PROPERTY_DISPLAY)
  public String getDisplayString(PatientGridColumn delegate) {
    if (delegate.getDatatype().equals(ColumnDatatype.OBS)) {
      Concept concept = ((ObsPatientGridColumn) delegate).getConcept();
      if (concept != null) {
        return concept.getDisplayString();
      }
    }
    String key = delegate.getDatatype().toString();
    if (delegate.getDatatype().equals(ColumnDatatype.ENC_AGE)) {
      Boolean ageRange = ((AgeAtEncounterPatientGridColumn) delegate).getConvertToAgeRange();
      key = Boolean.TRUE.equals(ageRange) ? "ENC_AGE_RANGE" : "ENC_AGE";
    }
    try {
      return Context.getMessageSourceService().getMessage("column." + key, null, Context.getLocale());
    } catch (NoSuchMessageException e) {
      return delegate.getName();
    }
  }

  /**
   * @see DelegatingSubResource#getParent(Object)
   */
  @Override
  public PatientGrid getParent(PatientGridColumn instance) {
    return instance.getPatientGrid();
  }

  /**
   * @see DelegatingSubResource#setParent(Object, Object)
   */
  @Override
  public void setParent(PatientGridColumn instance, PatientGrid parent) {
    instance.setPatientGrid(parent);
  }

  /**
   * @see DelegatingSubResource#doGetAll(Object, RequestContext)
   */
  @Override
  public PageableResult doGetAll(PatientGrid parent, RequestContext context) throws ResponseException {
    List<PatientGridColumn> columns = new ArrayList();
    if (parent != null) {
      for (PatientGridColumn column : parent.getColumns()) {
        columns.add(column);
      }
    }

    return new NeedsPaging(columns, context);
  }

  /**
   * @see DelegatingSubResource#getByUniqueId(String)
   */
  @Override
  public PatientGridColumn getByUniqueId(String uniqueId) {
    return Context.getService(PatientGridService.class).getPatientGridColumnByUuid(uniqueId);
  }

  /**
   * @see DelegatingSubResource#delete(Object, String, RequestContext)
   */
  @Override
  protected void delete(PatientGridColumn delegate, String reason, RequestContext context) throws ResponseException {
    purge(delegate, context);
  }

  /**
   * @see DelegatingSubResource#save(Object)
   */
  @Override
  public PatientGridColumn save(PatientGridColumn delegate) {
    delegate.getPatientGrid().addColumn(delegate);
    Context.getService(PatientGridService.class).savePatientGrid(delegate.getPatientGrid());
    return delegate;
  }

  /**
   * @see DelegatingSubResource#purge(Object, RequestContext)
   */
  @Override
  public void purge(PatientGridColumn delegate, RequestContext context) throws ResponseException {
    delegate.getPatientGrid().removeColumn(delegate);
    Context.getService(PatientGridService.class).savePatientGrid(delegate.getPatientGrid());
  }

  /**
   * @see DelegatingSubResource#getGETModel(Representation)
   */
  @Override
  public Model getGETModel(Representation rep) {
    ModelImpl model = (ModelImpl) super.getGETModel(rep);
    model.property("name", new StringProperty());
    model.property("uuid", new StringProperty());
    model.property(PatientGridConstants.PROP_DATATYPE, new EnumProperty(ColumnDatatype.class));
    model.property(PatientGridConstants.PROP_HIDDEN, new BooleanProperty());
    model.property(PatientGridConstants.PROP_DESCRIPTION, new StringProperty());
    model.property(PatientGridConstants.PROP_FILTERS,
        new ArrayProperty(new RefProperty("#/definitions/PatientgridPatientgridFilterGet")));
    return model;
  }

  /**
   * @see DelegatingSubResource#getCREATEModel(Representation)
   */
  @Override
  public Model getCREATEModel(Representation rep) {
    ModelImpl model = new ModelImpl();
    model.property("name", new StringProperty().required(true));
    model.property(PatientGridConstants.PROP_DATATYPE, new EnumProperty(ColumnDatatype.class).required(true));
    model.property(PatientGridConstants.PROP_HIDDEN, new BooleanProperty()._default(false));
    model.property(PatientGridConstants.PROP_DESCRIPTION, new StringProperty());
    model.property(PatientGridConstants.PROP_FILTERS,
        new ArrayProperty(new RefProperty("#/definitions/PatientgridPatientgridFilterCreate")));
    model.required(PatientGridConstants.PROP_FILTERS);
    return model;
  }

}
