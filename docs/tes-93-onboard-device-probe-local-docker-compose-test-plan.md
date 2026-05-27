# Test Plan: Onboard Device-Probe Local Docker-Compose Environment

## Summary
Test the setup of the Device-Probe local docker-compose environment.

## Test cases
1. Successful setup of local docker-compose environment
2. Understanding of Hazelcast map names and probe aggregation state

## Staging setup
* Queue names: probe.commands, normalize.ingest
* Cassandra table: device_probe_local_environment
* Actuator endpoint: /device-probe/local-environment

## Pass criteria
* Device-Probe local environment setup complete
* Hazelcast map names and probe aggregation state understood