# Summary
Validate Schema-Normalizer golden-file tests for Arista EOS devices.

# Test cases
1. Successful validation of Schema-Normalizer golden-file tests
2. No errors or warnings in test results

# Staging setup
* Queue names: normalize.ingest
* Cassandra table: arista_eos_golden_file_test
* Actuator endpoint: /actuator/health

# Pass criteria
* Successful validation of Schema-Normalizer golden-file tests
* No errors or warnings in test results