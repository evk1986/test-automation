# Summary
The purpose of this test plan is to verify the local development environment for Device-Probe.

# Test cases
1. Verify that the local development environment is set up correctly.
2. Verify that the Device-Probe service is running and responding to requests.

# Staging setup
* Queue names: probe.commands, normalize.ingest
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status

# Pass criteria
* The Device-Probe service is running and responding to requests.
* The local development environment is set up correctly.