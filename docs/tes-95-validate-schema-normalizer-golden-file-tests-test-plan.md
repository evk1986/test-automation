# Test Plan: Validate Schema-Normalizer Golden-File Tests for Arista EOS
## Summary
This test plan is designed to validate the Schema-Normalizer golden-file tests for Arista EOS devices.
## Test Cases
1. Retrieve Arista EOS golden file test data from repository
2. Perform validation logic on the retrieved data
3. Verify that the validation result is correct
## Staging Setup
* Queue names: normalize.ingest
* Cassandra table: normalized_records
* Actuator endpoint: /actuator/health
## Pass Criteria
* Successful validation of Schema-Normalizer golden-file tests
* No errors or warnings in test results