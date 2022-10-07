# Rest API Resources

## Patient Grid

Encapsulates metadata about a single patient grid

### Properties

<i style='color:red'>*</i> (Indicates a required property)

<i style='color:red'>*</i>`name` A unique grid name, uniqueness technically applies only among non-retired grids

`description` precise grid description

<i style='color:red'>*</i>`columns` A list or an array of column metadata where each element is metadata for a single
column on the grid, see [Grid Column Resource](#grid-column) for properties

`owner` The user the grid belongs to, a null value is interpreted as a system grid visible to everyone

`shared` Specifies if a grid is shared or not, defaults to false

**Note** This resource inherits all other standard metadata properties

## Grid Column

Encapsulates metadata about a single column on a patient grid

### Properties

<i style='color:red'>*</i>`name` A unique column name, only needs to be unique in the context of a single grid

`description` precise column description

<i style='color:red'>*</i>`type` A global discriminator field used by the web service API to determine what subclass to
instantiate for a resource that has a class hierarchy, the supported values are `obscolumn` for a column that holds an
observation value e.g. form field value that hold an obs value, `agecolumn` for a column that holds an age or age range
value and finally `column` for all other columns.

<i style='color:red'>*</i>`datatype` A value from the following possible values NAME, GENDER, ENC_DATE, ENC_AGE, OBS,
DATAFILTER_LOCATION, DATAFILTER_COUNTRY

`hidden` Specifies if a column is hidden or not, defaults to false

`filters`A list or an array of filter metadata where each element is metadata for a single filter on the column, see
[Grid Filter Resource](#grid-filter) for properties

### Other Encounter Date Column Properties

The properties below apply to a grid column where the _type_ property value is set to `encounterdatecolumn`

<i style='color:red'>*</i>`encounterType` The type of the encounters to match when evaluating the column date values

### Other Obs Column Properties

The properties below apply to a grid column where the _type_ property value is set to `obscolumn`

<i style='color:red'>*</i>`encounterType` The type of the encounters to match when evaluating the column obs values

<i style='color:red'>*</i>`concept` The question concept to match when evaluating the column obs values, MUST be a
concept UUID for create or update operations

### Other Age Obs Column Properties

The properties below apply to a grid column where the _type_ property value is set to `agecolumn`

<i style='color:red'>*</i>`encounterType` The type of the encounters to match when evaluating the column age values

`convertToAgeRange` If set to true ages get converted to age range based on the age ranges defined in the system, see
[Age Range Resource](#age-range) for how ranges work.

**Note** This resource inherits all other standard resource properties

## Grid Filter

Encapsulates metadata about a single column on a patient grid, all filters are implemented with equals operator

### Properties

<i style='color:red'>*</i>`name` A unique filter name, only needs to be unique in the context of a single column

<i style='color:red'>*</i>`operand` The value to match column values against

<i style='color:red'>*</i>`column` The grid column on which to apply the filter

## Age Range

A read-only resource encapsulating metadata for an age range definition, for more on how the possible age range
definitions are configured refer to the [age range global property](../../../README.md#age-ranges).

### Properties

`minAge` minimum age value

`minAgeUnit` minimum age unit, only YEARS is currently supported

`maxAge` maximum age value

`maxAgeUnit` maximum age unit, only YEARS is currently supported

`label` name for the age range

`display` usually same as label

## Grid Report

A read-only resource encapsulating report data for a specific patient grid.

### Properties

`patientGrid` The patient grid that was evaluated as a ref [representation](https://wiki.openmrs.org/x/P4IaAQ)

`report` The actual grid report data, it is a list of all patient data where each element in the list us data for a
single patient, the element is a map of column names and their respective data values.

**Note** Obs values in the report are trimmed down to a custom representation specific to this module with the
properties below,

`uuid` The uuid of the observation

`concept` The question concept uuid

`value` Display obs value, for a coded obs value the payload contains the answer concept's uuid and display

`formFieldPath` The field path on the form the observation was captured

`formFieldNamespace` The field namespace on the form the observation was captured

`encounter` A map with 2 entries, one entry has the key named `uuid` while its value is the uuid of the encounter the
observation belongs to, the other entry has key named `encounterType` while its value is the uuid of the encounter type
the observation belongs to.

## Grid Download

A read-only resource encapsulating downloadable data for a specific patient grid.

### Properties

`patientGrid` The patient grid that was evaluated as a ref [representation](https://wiki.openmrs.org/x/P4IaAQ)

`report` The actual grid report data, it is a list of all patient data where each element in the list is data for a
single patient, the element is a map of column names and their respective data values. The report contains extra map
entries i.e. one for each encounter type, if you generated your form fields based on a form, then each encounter type
would match that of its respective form. Therefore, for these extra column entries, the key is the uuid of the encounter
type and the value is a list of observation data for each encounter matching the encounter type for the patient. It
means each item in the list is a list of observations for a single encounter matching the encounter type.

**Note** Obs values in the report are trimmed down to a custom representation specific to this module with the
properties below,

`uuid` The uuid of the observation

`concept` The question concept uuid

`value` Display obs value

`formFieldPath` The field path on the form the observation was captured

`formFieldNamespace` The field namespace on the form the observation was captured

`encounter` A map with 2 entries, one entry has the key named `uuid` while its value is the uuid of the encounter the
observation belongs to, the other entry has key named `encounterType` while its value is the uuid of the encounter type
the observation belongs to.
