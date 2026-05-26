# Summary
This test plan covers the implementation of exponential-backoff retry in Device-Probe NETCONF worker.

# Test cases
1. Test that the retry handler routes exhausted-retry batches to the dead-letter queue with the device batch ID.
2. Test that the retry handler correctly implements exponential backoff for NETCONF session timeouts on IOS-XR devices.

# Staging setup
* Queue names: probe.commands, platform.results.dlq
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status

# Pass criteria
* The retry handler successfully routes exhausted-retry batches to the dead-letter queue with the device batch ID.
* The retry handler correctly implements exponential backoff for NETCONF session timeouts on IOS-XR devices.