# REST API Endpoints

## Patient Grid
### Fetch All Patient Grids
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid` (**Replace** SERVER_URL)

**HTTP Method** `GET`

**Example Response** See [Patient Grid Resource](../resources/README.md#patient-grid) (Ref [Representation](https://wiki.openmrs.org/x/P4IaAQ))

```
{
  "results": [
    {
      "uuid": "1d6c993e-c2cc-11de-8d13-0010c6dffd0a",
      "display": "My Patients",
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/1d6c993e-c2cc-11de-8d13-0010c6dffd0a"
        }
      ]
    },
    {
      "uuid": "2d6c993e-c2cc-11de-8d13-0010c6dffd0a",
      "display": "Male Patients",
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a"
        }
      ]
    }
  ]
}
```

### Fetch A Single Grid
To Include all the column and filter metadata for the grid, note that we fetch the full representation otherwise you can
exclude it the request parameter.

**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}?v=full`

**HTTP Method** `GET`

**Example Response** See [Patient Grid Resource](../resources/README.md#patient-grid) (Full [Representation](https://wiki.openmrs.org/x/P4IaAQ))
```
{
  "uuid": "2d6c993e-c2cc-11de-8d13-0010c6dffd0a",
  "display": "Male Patients",
  "name": "Male Patients",
  "description": "Male patient grid",
  "retired": false,
  "auditInfo": {
    "creator": {
      "uuid": "1010d442-e134-11de-babe-001e378eb67e",
      "display": "admin",
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/user/1010d442-e134-11de-babe-001e378eb67e"
        }
      ]
    },
    "dateCreated": "2022-08-28T00:00:00.000-0500",
    "changedBy": null,
    "dateChanged": null
  },
  "owner": {
    "uuid": "c1d8f5c2-e131-11de-babe-001e378eb67e",
    "display": "bruno",
    "links": [
      {
        "rel": "self",
        "uri": "{SERVER_URL}/ws/rest/v1/user/c1d8f5c2-e131-11de-babe-001e378eb67e"
      }
    ]
  },
  "columns": [
    {
      "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
      "display": "gender",
      "name": "gender",
      "description": "patient gender",
      "datatype": "GENDER",
      "filters": [
        {
          "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0c",
          "display": "is male",
          "name": "is male",
          "column": {
            "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
            "display": "gender",
            "links": [
              {
                "rel": "self",
                "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
              }
            ],
            "type": "column"
          },
          "operand": "M",
          "links": [
            {
              "rel": "self",
              "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c"
            },
            {
              "rel": "full",
              "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
            }
          ],
          "resourceVersion": "1.8"
        }
      ],
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
        },
        {
          "rel": "full",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b?v=full"
        }
      ],
      "type": "column",
      "resourceVersion": "1.8"
    },
    {
      "uuid": "2f6c993e-c2cc-11de-8d13-0010c6dffd0b",
      "display": "civilStatus",
      "name": "civilStatus",
      "description": "patient civil status",
      "datatype": "OBS",
      "filters": [
      ],
      "concept": {
        "uuid": "89ca642a-dab6-4f20-b712-e12ca4fc6d36",
        "display": "CIVIL STATUS",
        "links": [
          {
            "rel": "self",
            "uri": "{SERVER_URL}/ws/rest/v1/concept/89ca642a-dab6-4f20-b712-e12ca4fc6d36"
          }
        ]
      },
      "encounterType": {
        "uuid": "19218f76-6c39-45f4-8efa-4c5c6c199f50",
        "display": "Initial",
        "links": [
          {
            "rel": "self",
            "uri": "{SERVER_URL}/ws/rest/v1/encountertype/19218f76-6c39-45f4-8efa-4c5c6c199f50"
          }
        ]
      },
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/2f6c993e-c2cc-11de-8d13-0010c6dffd0b"
        },
        {
          "rel": "full",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/2f6c993e-c2cc-11de-8d13-0010c6dffd0b?v=full"
        }
      ],
      "type": "obscolumn",
      "resourceVersion": "1.8"
    }
  ],
  "links": [
    {
      "rel": "self",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a"
    }
  ],
  "resourceVersion": "1.8"
}
```

### Create New Patient Grid
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid`

**HTTP Method** `POST`

**Example Payload**
```
{
  "name": "test",
  "description": "test description",
  "columns": [
    {
      "type": "column",
      "name": "name",
      "datatype": "NAME"
    },
    {
      "type": "obscolumn",
      "name": "weight",
      "datatype": "OBS",
      "concept": "c607c80f-1ea9-4da3-bb88-6276ce8868dd",
      "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50"
    },
    {
      "type": "agecolumn",
      "name": "age",
      "datatype": "ENC_AGE",
      "encounterType": "19218f76-6c39-45f4-8efa-4c5c6c199f50",
      "filters": [
        {
          "name": "equal 12",
          "operand": "12"
        }
      ]
    }
  ]
}
```

**Example Response:** (Default [Representation](https://wiki.openmrs.org/x/P4IaAQ))
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
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/7e6add6c-ca68-443a-92fa-9395845bc383"
    },
    {
      "rel": "full",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/7e6add6c-ca68-443a-92fa-9395845bc383?v=full"
    }
  ],
  "resourceVersion": "1.8"
}
```

### Delete An Existing Grid
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}`

**HTTP Method** `DELETE`

**Request Parameter**
`reason` Reason for deleting the grid

## Grid Column
### Fetch A Single Column
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/column/{COLUMN_UUID}`

**HTTP Method** `GET`

**Example Response** See [Grid Column Resource](../resources/README.md#grid-column), (Default [Representation](https://wiki.openmrs.org/x/P4IaAQ))
```
{
  "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
  "display": "gender",
  "name": "gender",
  "description": "patient gender",
  "datatype": "GENDER",
  "filters": [
    {
      "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0c",
      "display": "is male",
      "name": "is male",
      "column": {
        "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
        "display": "gender",
        "links": [
          {
            "rel": "self",
            "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
          }
        ],
        "type": "column"
      },
      "operand": "M",
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c"
        },
        {
          "rel": "full",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
        }
      ],
      "resourceVersion": "1.8"
    }
  ],
  "links": [
    {
      "rel": "self",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
    },
    {
      "rel": "full",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b?v=full"
    }
  ],
  "type": "column",
  "resourceVersion": "1.8"
}
```

### Fetch All Columns For A Grid
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/column`

**HTTP Method** `GET`

**Example Response** See [Grid Column Resource](../resources/README.md#grid-column), (Default [Representation](https://wiki.openmrs.org/x/P4IaAQ))
```
{
  "results": [
    {
      "uuid": "0e6c993e-c2cc-11de-8d13-0010c6dffd0b",
      "display": "name",
      "name": "name",
      "description": "patient name",
      "datatype": "NAME",
      "filters": [
        
      ],
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/0e6c993e-c2cc-11de-8d13-0010c6dffd0b"
        },
        {
          "rel": "full",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/0e6c993e-c2cc-11de-8d13-0010c6dffd0b?v=full"
        }
      ],
      "type": "column",
      "resourceVersion": "1.8"
    },
    {
      "uuid": "2f6c993e-c2cc-11de-8d13-0010c6dffd0b",
      "display": "civilStatus",
      "name": "civilStatus",
      "description": "patient civil status",
      "datatype": "OBS",
      "filters": [
        {
          "uuid": "2f6c993e-c2cc-11de-8d13-0010c6dffd0c",
          "display": "is single",
          "name": "is single",
          "column": {
            "uuid": "2f6c993e-c2cc-11de-8d13-0010c6dffd0b",
            "display": "civilStatus",
            "links": [
              {
                "rel": "self",
                "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/2f6c993e-c2cc-11de-8d13-0010c6dffd0b"
              }
            ],
            "type": "obscolumn"
          },
          "operand": "32d3611a-6699-4d52-823f-b4b788bac3e3",
          "links": [
            {
              "rel": "self",
              "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/2f6c993e-c2cc-11de-8d13-0010c6dffd0c"
            },
            {
              "rel": "full",
              "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/2f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
            }
          ],
          "resourceVersion": "1.8"
        },
        {
          "uuid": "3f6c993e-c2cc-11de-8d13-0010c6dffd0c",
          "display": "is divorced",
          "name": "is divorced",
          "column": {
            "uuid": "2f6c993e-c2cc-11de-8d13-0010c6dffd0b",
            "display": "civilStatus",
            "links": [
              {
                "rel": "self",
                "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/2f6c993e-c2cc-11de-8d13-0010c6dffd0b"
              }
            ],
            "type": "obscolumn"
          },
          "operand": "92afda7c-78c9-47bd-a841-0de0817027d4",
          "links": [
            {
              "rel": "self",
              "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/3f6c993e-c2cc-11de-8d13-0010c6dffd0c"
            },
            {
              "rel": "full",
              "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/3f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
            }
          ],
          "resourceVersion": "1.8"
        }
      ],
      "concept": {
        "uuid": "89ca642a-dab6-4f20-b712-e12ca4fc6d36",
        "display": "CIVIL STATUS",
        "links": [
          {
            "rel": "self",
            "uri": "{SERVER_URL}/ws/rest/v1/concept/89ca642a-dab6-4f20-b712-e12ca4fc6d36"
          }
        ]
      },
      "encounterType": {
        "uuid": "19218f76-6c39-45f4-8efa-4c5c6c199f50",
        "display": "Initial",
        "links": [
          {
            "rel": "self",
            "uri": "{SERVER_URL}/ws/rest/v1/encountertype/19218f76-6c39-45f4-8efa-4c5c6c199f50"
          }
        ]
      },
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/2f6c993e-c2cc-11de-8d13-0010c6dffd0b"
        },
        {
          "rel": "full",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/2f6c993e-c2cc-11de-8d13-0010c6dffd0b?v=full"
        }
      ],
      "type": "obscolumn",
      "resourceVersion": "1.8"
    }
  ]
}
```

### Add New Column To A Grid
Currently, not supported, there is a ticket to to add support for this.

### Modify An Existing Column
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/column/{COLUMN_UUID}`

**HTTP Method** `POST`

**Example Payload**
```
 {
  "name": "New Name"
}
```

**Example Response**
```
{
  "uuid": "1e6c993e-c2cc-11de-8d13-0010c6dffd0b",
  "display": "New Name",
  "name": "New Name",
  "description": "patient name",
  "datatype": "NAME",
  "filters": [
    
  ],
  "links": [
    {
      "rel": "self",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/1d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1e6c993e-c2cc-11de-8d13-0010c6dffd0b"
    },
    {
      "rel": "full",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/1d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1e6c993e-c2cc-11de-8d13-0010c6dffd0b?v=full"
    }
  ],
  "type": "column",
  "resourceVersion": "1.8"
}
```

### Remove Column From Grid
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/column/{COLUMN_UUID}`

**HTTP Method** `DELETE`

## Grid Filter
### Fetch A Single Filter
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/filter/{FILTER_UUID}`

**HTTP Method** `GET`

**Example Response** See [Grid Filter Resource](../resources/README.md#grid-filter), (Default [Representation](https://wiki.openmrs.org/x/P4IaAQ))
```
{
  "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0c",
  "display": "is male",
  "name": "is male",
  "column": {
    "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
    "display": "gender",
    "links": [
      {
        "rel": "self",
        "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
      }
    ],
    "type": "column"
  },
  "operand": "M",
  "links": [
    {
      "rel": "self",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c"
    },
    {
      "rel": "full",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
    }
  ],
  "resourceVersion": "1.8"
}
```

### Fetch All Filters For A Grid
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/filter`

**HTTP Method** `GET`

**Example Response:** See [Grid Filter Resource](../resources/README.md#grid-filter), (Default [Representation](https://wiki.openmrs.org/x/P4IaAQ))
```
{
  "results": [
    {
      "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0c",
      "display": "is male",
      "name": "is male",
      "column": {
        "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
        "display": "gender",
        "links": [
          {
            "rel": "self",
            "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
          }
        ],
        "type": "column"
      },
      "operand": "M",
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c"
        },
        {
          "rel": "full",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
        }
      ],
      "resourceVersion": "1.8"
    },
    {
      "uuid": "2f6c993e-c2cc-11de-8d13-0010c6dffd0c",
      "display": "is single",
      "name": "is single",
      "column": {
        "uuid": "2f6c993e-c2cc-11de-8d13-0010c6dffd0b",
        "display": "civilStatus",
        "links": [
          {
            "rel": "self",
            "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/2f6c993e-c2cc-11de-8d13-0010c6dffd0b"
          }
        ],
        "type": "obscolumn"
      },
      "operand": "32d3611a-6699-4d52-823f-b4b788bac3e3",
      "links": [
        {
          "rel": "self",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/2f6c993e-c2cc-11de-8d13-0010c6dffd0c"
        },
        {
          "rel": "full",
          "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/2f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
        }
      ],
      "resourceVersion": "1.8"
    }
  ]
}
```

### Add New Filter To A Grid Column
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/filter`

**HTTP Method** `POST`

**Example Payload**
```
{
  "name": "male patients",
  "column": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
  "operand": "M"
}
```

**Example Response:**
```
{
  "uuid": "84e778f2-9794-4d5c-93fd-c646357eab5a",
  "display": "male patients",
  "name": "male patients",
  "column": {
    "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
    "display": "gender",
    "links": [
      {
        "rel": "self",
        "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
      }
    ],
    "type": "column"
  },
  "operand": "M",
  "links": [
    {
      "rel": "self",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/84e778f2-9794-4d5c-93fd-c646357eab5a"
    },
    {
      "rel": "full",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/84e778f2-9794-4d5c-93fd-c646357eab5a?v=full"
    }
  ],
  "resourceVersion": "1.8"
}
```

### Modify An Existing Filter
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/filter/{FILTER_UUID}`
**(Replace GRID_UUID and FILTER_UUID)**

**HTTP Method** `POST`

**Example Payload** (ONLY include properties you need to modify or set)
```
{
  "name": "New Name",
  "operand": "F"
}
```

**Example Response**
```
{
  "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0c",
  "display": "New Name",
  "name": "New Name",
  "column": {
    "uuid": "1f6c993e-c2cc-11de-8d13-0010c6dffd0b",
    "display": "gender",
    "links": [
      {
        "rel": "self",
        "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/column/1f6c993e-c2cc-11de-8d13-0010c6dffd0b"
      }
    ],
    "type": "column"
  },
  "operand": "F",
  "links": [
    {
      "rel": "self",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c"
    },
    {
      "rel": "full",
      "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/2d6c993e-c2cc-11de-8d13-0010c6dffd0a/filter/1f6c993e-c2cc-11de-8d13-0010c6dffd0c?v=full"
    }
  ],
  "resourceVersion": "1.8"
}
```

### Remove Filter From Grid
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/filter/{FILTER_UUID}`

**HTTP Method** `DELETE`

## Age Ranges
### Fetch All Age Ranges
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/agerange`

**HTTP Method** `GET`

**Example Response**

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

## Grid Report
### Run Grid Report
**Endpoint** `{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/{GRID_UUID}/report`

**HTTP Method** `GET`

**Request Parameters**
`refresh` If set to true, any caches from previous runs are discarded, i.e. the patient grid is re-evaluate to produce
fresh data

**Example Response** See [Grid Report Resource](../resources/README.md#grid-report), (Ref [Representation](https://wiki.openmrs.org/x/P4IaAQ))

Please pay extra attention to obs column values, for more see the note on obs value properties under [Grid Report Resource](../resources/README.md#grid-report)
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
            "uri": "{SERVER_URL}/ws/rest/v1/patientgrid/patientgrid/1d6c993e-c2cc-11de-8d13-0010c6dffd0a"
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

## Grid Download
### Run Grid Download
