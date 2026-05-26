# Summary
This test plan covers the implementation of exponential-backoff retry in the Device-Probe NETCONF worker.

# Test cases
1. Successful NETCONF session establishment
2. Failed NETCONF session establishment with exponential backoff
3. Routing to dead-letter queue after max attempts

# Staging setup
* Queue names: probe.commands, platform.results.dlq
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status

# Pass criteria
* The NETCONF worker successfully establishes a session with exponential backoff.
* The worker routes to the dead-letter queue after max attempts.
