# Summary
This test plan is for the implementation of exponential backoff retry in the Device-Probe NETCONF worker.

# Test cases
1. Test that the retry handler routes exhausted-retry batches to the dead-letter queue with the device batch ID.
2. Test that the exponential backoff delay is correctly calculated and applied.

# Staging setup
* Queue names: probe.commands, platform.results.dlq
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status

# Pass criteria
* The retry handler correctly routes exhausted-retry batches to the dead-letter queue with the device batch ID.
* The exponential backoff delay is correctly calculated and applied.