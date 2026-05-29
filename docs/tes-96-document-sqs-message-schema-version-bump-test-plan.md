# Test Plan: SQS Message Schema Version Bump
## Summary
This test plan documents the testing process for the SQS message schema version bump for the Data-Enricher service.
## Test Cases
1. **Test Case 1: Update SQS Message Schema Version**
    * Preconditions: The Data-Enricher service is deployed and running.
    * Steps: Update the SQS message schema version in the Data-Enricher service.
    * Expected Result: The SQS message schema version is updated successfully.
2. **Test Case 2: Verify Updated Schema Version**
    * Preconditions: The Data-Enricher service is deployed and running with the updated SQS message schema version.
    * Steps: Verify that the updated schema version is being used by the service.
    * Expected Result: The updated schema version is being used by the service.
## Staging Setup
* The Data-Enricher service is deployed in a staging environment.
* The SQS message schema version is updated in the staging environment.
## Pass Criteria
* The SQS message schema version is updated successfully.
* The updated schema version is being used by the service.
