# Patient Grid Module
Provides an API for managing and evaluating a grid of patient data

# Packages

packages can be found on [GitHub Packages](https://github.com/orgs/icrc/packages?repo_name=openmrs-module-patientgrid).

# SNAPSHOT Deployment
A Snaphsot is deployed for each push on the `main` branch.

# Release
Releases are generated via the Worklow [`Do Release`](https://github.com/icrc/openmrs-module-patientgrid/actions/workflows/release.yml) defined 
in [Actions](https://github.com/icrc/openmrs-module-patientgrid/actions) Tab. 

A release, will:
1. Remove the SNAPSHOT in the artifact version
2. deploy the artifacts
3. Tag the sources and create a release
4. Move to the next SNAPSHOT version in a separate branch named `move-to-X.Y.Z-SNAPSHOT`
5. Create a PR


# Rest API Documentation

## Age Ranges
### Fetch All Age Ranges
**Endpoint:** ```[HOST_URL]/openmrs/ws/rest/v1/patientgrid/agerange```

**HTTP Method** ```GET```

**Response:**

```
{
  "results": [
    {
      "minAge": 0,
      "maxAge": 17,
      "label": "<18yrs",
      "display": "<18yrs"
    },
    {
      "minAge": 18,
      "maxAge": null,
      "label": "18+",
      "display": "18+"
    }
  ]
}
```
