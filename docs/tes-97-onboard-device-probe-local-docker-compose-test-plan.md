# Summary
This test plan covers the onboard process of the Device-Probe local docker-compose environment.

# Test cases
1. Successful onboard of Device-Probe local docker-compose environment
2. No errors or warnings in environment setup

# Staging setup
* Queue names: probe.commands, normalize.ingest, enrich.pipeline, platform.results.dlq
* Cassandra table: device_probe_onboard
* Actuator endpoint: /actuator/health

# Pass criteria
* The Device-Probe local docker-compose environment is successfully onboarded
* No errors or warnings are encountered during environment setup