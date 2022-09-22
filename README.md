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
### Grid Report Cache Directory

**Property Name** `patientgrid.cacheDirectory`

The location of the directory to use to cache grid report data, it can be a folder name which will be located in the 
application data directory otherwise an absolute path.

Defaults to a folder named `.report_cache` in the application data directory

### Age Ranges

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
Please see [Rest API Documentation](omod/README.md)
