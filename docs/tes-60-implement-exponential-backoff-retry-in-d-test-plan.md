# Test Plan: Implement Exponential-Backoff Retry in Device-Probe NETCONF Worker
## Summary
This test plan covers the implementation of exponential-backoff retry in the Device-Probe NETCONF worker.
## Test Cases
1. Successful retry
2. Exhausted-retry batches are routed to the dead-letter queue
## Staging Setup
* Queue names: probe.commands, platform.results.dlq
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health
## Pass Criteria
* The retry handler is implemented with exponential backoff.
* Exhausted-retry batches are routed to the dead-letter queue.