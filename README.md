# Patient Grid Module
Provides an API for managing and evaluating a grid of patient data

# Packages

packages can be found on [GitHub Packages](https://github.com/orgs/icrc/packages?repo_name=openmrs-module-patientgrid).

# SNAPSHOT and Release Deployments
Deployments are done for ICRC intern CI/CD Tasks.

# How to integration the module in your project.

## Add the repository to your main `pom.xml` file

```xml
    <repository>
        <id>openmrs-module-patientgrid</id>
        <name>openmrs-module-patientgrid</name>
        <url>https://maven.pkg.github.com/icrc/openmrs-module-patientgrid</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
```
## Modify the `settings.xml` file
Packages are public but a Token is nevertheless required ( Github packages limitation).
You will have to generate a token from a Github package (with read access to packages) and add these lines to the `settings.xml` file:

```xml
<server>
    <id>openmrs-module-patientgrid</id>
    <username>#{GITHUB_USERNAME}#</username>
    <password>#{GITHUB_TOKEN}#</password>
</server>
```

# Global Properties

## Period range

**Property Name** `patientgrid.defaultPeriod`

Specifies the default period to use if no period defined in the frontend query in order to limit the number of rows.
By default, the last 30 days (LASTTHIRTYDAYS) will be used.
Current supported values are defined in the class `DateRangeType`: LASTSEVENDAYS, LASTTHIRTYDAYS, WEEKTODATE, MONTHTODATE,...

Defaults to a folder named `.report_cache` in the patientgrid folder in the application data directory

## Grid Report Cache Directory

**Property Name** `patientgrid.cacheDirectory`

Specifies the location of the directory to use to cache grid report data, it can be an absolute path otherwise a folder 
name which will be located in the application data directory.

Defaults to a folder named `.report_cache` in the patientgrid folder in the application data directory

**Property Name** `patientgrid.maxCacheFileAge`

Specifies the max age (in hour) for cache files containing grid report data. Files older than this value will be deleted (via the task `org.openmrs.module.patientgrid.cache.CleanCacheTask`)  

Defaults to 48h

## Age Ranges

**Property Name** `patientgrid.age.ranges`

Specifies a comma separated list of age range definitions in years to be used by grid columns that hold age range 
values, in the future we might add support for months, below is an example list of age range definitions for the global 
property value,

`0-18:<18, 19-29:Youth, 30-39, 40-54:Middle Aged, 55+`

Below are the key things to note from the example above,
1. Each item in the list is an age range definition and they are separated by a comma
2. Each definition can have up to 2 fields separated by a full colon, where the first field is the actual age range, the 
   second field is a label which defaults to the first field if not specified, the label is optional EXCEPT for the last
   definition, in our example above the third definition is an example of a definition with no label specified, 
   therefore it defaults to 30-39.
3. Each age range field is composed of 2 sub fields, a minimum and maximum age in this order separated by a hyphen 
   EXCEPT the last definition for which one is only required to provide a label, the minimum value for the last 
   definition is computed by incrementing by 1 the maximum age for the definition preceeding it in the list and it has 
   no maximum boundary.

# Rest API Documentation
For more details about the OpenMRS REST Web Services API, please refer to the links below,
- [REST Web Services API For Clients](https://wiki.openmrs.org/x/P4IaAQ)
- [REST Web Services Resource Representation](https://wiki.openmrs.org/x/P4IaAQ)
- [OpenMRS REST API](https://rest.openmrs.org/#openmrs-rest-api)

**NOTE** Authentication, resource representation, searching, paging, limiting results etc are not covered here, please
refer to the resources above.

## Resources
1. [Patient Grid](docs/rest/resources/README.md#patient-grid)
2. [Grid Column](docs/rest/resources/README.md#grid-column)
3. [Grid Filter](docs/rest/resources/README.md#grid-filter)
4. [Age Range](docs/rest/resources/README.md#age-range)
5. [Grid Report](docs/rest/resources/README.md#grid-report)
6. [Grid Download](docs/rest/resources/README.md#grid-download)

## Endpoints
### Patient Grid
1. [Fetch All Patient Grids](docs/rest/endpoints/README.md#fetch-all-patient-grids)
2. [Fetch A Single Grid](docs/rest/endpoints/README.md#fetch-a-single-grid)
3. [Create New Patient Grid](docs/rest/endpoints/README.md#create-new-patient-grid)
4. [Delete An Existing Grid](docs/rest/endpoints/README.md#delete-an-existing-grid)

### Grid Column
1. [Fetch A Single Column](docs/rest/endpoints/README.md#fetch-a-single-column)
2. [Fetch All Columns For A Grid](docs/rest/endpoints/README.md#fetch-all-columns-for-a-grid)
3. [Add New Column To A Grid](docs/rest/endpoints/README.md#add-new-column-to-a-grid)
4. [Modify An Existing Column](docs/rest/endpoints/README.md#modify-an-existing-column)
5. [Remove Column From Grid](docs/rest/endpoints/README.md#remove-column-from-grid)

### Grid Filter
1. [Fetch A Single Filter](docs/rest/endpoints/README.md#fetch-a-single-filter)
2. [Fetch All Filters For A Grid](docs/rest/endpoints/README.md#fetch-all-filters-for-a-grid)
3. [Add New Filter To A Grid Column](docs/rest/endpoints/README.md#add-new-filter-to-a-grid-column)
4. [Modify An Existing Filter](docs/rest/endpoints/README.md#modify-an-existing-filter)
5. [Remove Filter From Grid](docs/rest/endpoints/README.md#remove-filter-from-grid)

### Age Range
1. [Fetch All Age Ranges](docs/rest/endpoints/README.md#fetch-all-age-ranges)

### Grid Report
1. [Run Grid Report](docs/rest/endpoints/README.md#run-grid-report)

### Encounter History
1. [Fetch Encounters By Patient And Type](docs/rest/endpoints/README.md#fetch-encounters-by-patient-and-type)

### Grid Download
1. [Run Grid Download](docs/rest/endpoints/README.md#run-grid-download)
