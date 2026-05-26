# Summary
The purpose of this test plan is to validate the Fleet-Orchestrator batch config on staging cron jobs.

# Test cases
1. Validate batch config for a valid batch ID
2. Validate batch config for an invalid batch ID
3. Validate batch config for a batch ID with no associated batch config

# Staging setup
* Queue name: probe.commands
* Cassandra table: batch_config
* Actuator endpoint: /actuator/health

# Pass criteria
* The batch config is validated correctly for all test cases
* The actuator endpoint returns a 200 OK status code
