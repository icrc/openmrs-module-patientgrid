# Rest API Documentation
For more details about the OpenMRS REST Web Services API, please refer to the links below,
- [REST Web Services API For Clients](https://wiki.openmrs.org/x/P4IaAQ)
- [REST Web Services Resource Representation](https://wiki.openmrs.org/x/P4IaAQ)
- [OpenMRS REST API](https://rest.openmrs.org/#openmrs-rest-api)

**NOTE** Authentication, resource representation, searching, paging, limiting results etc are not covered here, please
refer to the resources above.

## Table of Contents

1. [API Resources](#api-resources)
   1. [Patient Grid](#patient-grid) 
   2. [Patient Grid Column](#patient-grid-column)
   3. [Age Range](#age-range)
   4. [Patient Grid Report](#patient-grid-report)
   5. [Patient Grid Download](#)
2. [Operations](#operations)
   1. [Patient Grid Operations](#patient-grid-operations)
   2. [Patient Grid Report Operations](#patient-grid-report-operations)
   3. [Age Range Operations](#age-ranges-operations)

## API Resources

### Patient Grid
Encapsulates metadata about a single patient grid

#### Properties
<i style='color:red'>*</i> (Indicates a required property)

<i style='color:red'>*</i>`name` A unique grid name, uniqueness technically applies only among non-retired grids

`description` precise grid description

<i style='color:red'>*</i>`columns`
A list or an array of column metadata where each element is metadata for a single column, see [Patient Grid Column Resource](#patient-grid-column) for properties

`owner` The user the grid belongs to, a null value is interpreted as a system grid visible to everyone

**Note** This resource inherits all other standard metadata properties

### Patient Grid Column
Encapsulates metadata about a single patient grid column

#### Properties
<i style='color:red'>*</i>`name` A unique column name, only needs to be unique in the context of a single grid

`description` precise column description

<i style='color:red'>*</i>`type` A global discriminator field used by the web service API to determine what subclass to 
instantiate for a resource that has a class hierarchy, the supported values are `obscolumn` for a column that holds an 
observation value e.g. form field value that hold an obs value, `agecolumn` for a column that holds an age or age range 
value and finally `column` for all other columns.

<i style='color:red'>*</i>`datatype` A value from the following possible values NAME, GENDER, ENC_AGE, OBS, 
DATAFILTER_LOCATION, DATAFILTER_COUNTRY

#### Other Obs Column Properties
The properties below only apply to a grid column where the _type_ property value is set to `obscolumn`

<i style='color:red'>*</i>`encounterType` The type of the encounters where to match when evaluating the column obs values

<i style='color:red'>*</i>`concept` The question concept to match when evaluating the column obs values, MUST be a 
concept UUID for create or update [patient grid](#patient-grid-operations) and [column](#patient-grid-column-operations) 
operations


#### Other Obs Column Properties
The properties below only apply to a grid column where the _type_ property value is set to `agecolumn`

<i style='color:red'>*</i>`encounterType` The type of the encounters where to match when evaluating the column obs values

`convertToAgeRange` If set to true ages get converted to age range based on the age ranges defined in the system, see
[Age Range Resource](#age-range) for how ranges work.

**Note** This resource inherits all other standard resource properties

### Patient Grid Report
A read-only resource encapsulating report data for a specific patient grid.

#### Properties
`patientGrid` The patient grid that was evaluated as a ref [representation](https://wiki.openmrs.org/x/P4IaAQ)

`report` The actual grid report data, it is a list of all patient data where each element in the list us data for a
single patient, the element is a map of column names and their respective data values.


**Note** Obs values in the report are trimmed down to a custom representation specific to this module with the
properties below,

`uuid` The uuid of the observation

`concept` The question concept uuid

`value` Display obs value

`formPath` The field path on the form the observation was captured

`formNamespace` The field namespace on the form the observation was captured

`encounter` A map with 2 entries, one entry has the key named `uuid` while its value is the uuid of the encounter the
observation belongs to, the other entry has key named `encounterType` while its value is the uuid of the encounter type
the observation belongs to.

### Age Range
A read-only resource encapsulating metadata for an age range definition, for more on how the possible age range 
definitions are configured refer to the [age range global property](../README.md#age-ranges).

#### Properties
`minAge` minimum age value

`minAgeUnit` minimum age unit, only YEARS is currently supported

`maxAge` maximum age value

`maxAgeUnit` maximum age unit, only YEARS is currently supported

`label` name for the age range

`display` usually same as label

## Operations
### Patient Grid Operations
#### Fetch All Patient Grids
**Endpoint:** `SERVER_URL/ws/rest/v1/patientgrid/patientgrid` **(Replace SERVER_URL)**

**HTTP Method** `GET`

**Response:** (Default [Representation](https://wiki.openmrs.org/x/P4IaAQ))

```
{
  "results": [
    {
      "uuid": "1d6c993e-c2cc-11de-8d13-0010c6dffd0a",
      "display": "My Patients",
      "links": [
        {
          "rel": "self",
          "uri": "SERVER_URL/ws/rest/v1/patientgrid/patientgrid/1d6c993e-c2cc-11de-8d13-0010c6dffd0a"
        }
      ]
    },
    {
      "uuid": "2d6c993e-c2cc-11de-8d13-0010c6dffd0a",
      "display": "Male Patients",
      "links": [
        {
          "rel": "self",
          "uri": "SERVER_URL/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a"
        }
      ]
    }
  ]
}
```

#### Create Patient Grid
**Endpoint:** `SERVER_URL/ws/rest/v1/patientgrid/patientgrid` **(Replace SERVER_URL)**

**HTTP Method** `POST`

**Example Payload**
```
{
  "name" : "test",
  "description" : "test description",
  "columns" : [ {
    "type" : "column",
    "name" : "name",
    "datatype" : "NAME"
  }, {
    "type" : "obscolumn",
    "name" : "weight",
    "datatype" : "OBS",
    "concept" : "c607c80f-1ea9-4da3-bb88-6276ce8868dd",
    "encounterType" : "19218f76-6c39-45f4-8efa-4c5c6c199f50"
  }, {
    "type" : "agecolumn",
    "name" : "age",
    "datatype" : "ENC_AGE",
    "encounterType" : "19218f76-6c39-45f4-8efa-4c5c6c199f50"
  } ]
}
```

**Response:** (Ref [Representation](https://wiki.openmrs.org/x/P4IaAQ))
```
{
  "uuid": "7e6add6c-ca68-443a-92fa-9395845bc383",
  "display": "test",
  "name": "test",
  "description": "test description",
  "retired": false,
  "owner": null,
  "links": [
    {
      "rel": "self",
      "uri": "SERVER_URL/ws/rest/v1/patientgrid/patientgrid/7e6add6c-ca68-443a-92fa-9395845bc383"
    },
    {
      "rel": "full",
      "uri": "SERVER_URL/ws/rest/v1/patientgrid/patientgrid/7e6add6c-ca68-443a-92fa-9395845bc383?v=full"
    }
  ],
  "resourceVersion": "1.8"
}
```

### Patient Grid Report Operations
#### Run Patient Grid Report
**Endpoint:** `SERVER_URL/ws/rest/v1/patientgrid/patientgrid/PATIENT_GRID_UUID/report` **(Replace PATIENT_GRID_UUID)**

**HTTP Method** `GET`

**Request Parameters**
`refresh` If set to true, any caches from previous runs are discarded, i.e. the patient grid is re-evaluate to produce
fresh data

**Response:** See [Patient Grid Report Resource](#patient-grid-report), (Ref [Representation](https://wiki.openmrs.org/x/P4IaAQ))

Please pay extra attention to obs column values, for more see the note on obs value properties under [Patient Grid Report Resource](#patient-grid-report)
```
{
  "results": [
    {
      "patientGrid": {
        "uuid": "1d6c993e-c2cc-11de-8d13-0010c6dffd0a",
        "display": "My Patients",
        "links": [
          {
            "rel": "self",
            "uri": "SERVER_URL/ws/rest/v1/patientgrid/patientgrid/1d6c993e-c2cc-11de-8d13-0010c6dffd0a"
          }
        ]
      },
      "report": [
        {
          "country": "United States",
          "cd4": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "9619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "29218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "25fb7f47-b80a-4056-9285-bd798be13c63",
            "value": 1080.0,
            "concept": "a09ab2c5-878e-4905-b25d-5784167d0216"
          },
          "gender": "M",
          "name": "Johnny Test Doe",
          "weight": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "6619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "64fb7f47-b80a-4056-9285-bd798be13c63",
            "value": 72.0,
            "concept": "c607c80f-1ea9-4da3-bb88-6276ce8868dd"
          },
          "ageCategory": "18+",
          "uuid": "a7e04421-525f-442f-8138-05b619d16def",
          "ageAtInitial": 46,
          "structure": "Austin",
          "civilStatus": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "6619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "94fb7f47-b80a-4056-9285-bd798be13c63",
            "value": "SINGLE",
            "concept": "89ca642a-dab6-4f20-b712-e12ca4fc6d36"
          }
        },
        {
          "country": "United States",
          "cd4": null,
          "gender": "F",
          "name": "Collet Test Chebaskwony",
          "weight": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "7619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "74fb7f47-b80a-4056-9285-bd798be13c63",
            "value": 88.0,
            "concept": "c607c80f-1ea9-4da3-bb88-6276ce8868dd"
          },
          "ageCategory": "18+",
          "uuid": "5946f880-b197-400b-9caa-a3c661d23041",
          "ageAtInitial": 45,
          "structure": "Austin",
          "civilStatus": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "7619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "04fb7f47-b80a-4056-9285-bd798be13c63",
            "value": "MARRIED",
            "concept": "89ca642a-dab6-4f20-b712-e12ca4fc6d36"
          }
        },
        {
          "country": "Uganda",
          "cd4": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "2619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "29218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "15fb7f47-b80a-4056-9285-bd798be13c63",
            "value": 1060.0,
            "concept": "a09ab2c5-878e-4905-b25d-5784167d0216"
          },
          "gender": "M",
          "name": "Mr. Horatio Test Hornblower",
          "weight": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "4619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "44fb7f47-b80a-4056-9285-bd798be13c63",
            "value": 84.0,
            "concept": "c607c80f-1ea9-4da3-bb88-6276ce8868dd"
          },
          "ageCategory": "18+",
          "uuid": "da7f524f-27ce-4bb2-86d6-6d1d05312bd5",
          "ageAtInitial": 47,
          "structure": "Kampala",
          "civilStatus": {
            "formPath": null,
            "formNamespace": null,
            "encounter": {
              "uuid": "4619d653-393b-4118-9e83-a3715b82d4ac",
              "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50"
            },
            "uuid": "84fb7f47-b80a-4056-9285-bd798be13c63",
            "value": "SINGLE",
            "concept": "89ca642a-dab6-4f20-b712-e12ca4fc6d36"
          }
        }
      ],
      "resourceVersion": "1.8"
    }
  ]
}
```

### Age Ranges Operations
#### Fetch All Age Ranges
**Endpoint:** `SERVER_URL/ws/rest/v1/patientgrid/agerange`

**HTTP Method** `GET`

**Response:**

```
{
  "results": [
    {
      "minAge": 0,
      "minAgeUnit": "YEARS",
      "maxAge": 17,
      "maxAgeUnit": "YEARS",
      "label": "<18yrs",
      "display": "<18yrs"
    },
    {
      "minAge": 18,
      "minAgeUnit": "YEARS",
      "maxAge": null,
      "maxAgeUnit": "YEARS",
      "label": "18+",
      "display": "18+"
    }
  ]
}
```
