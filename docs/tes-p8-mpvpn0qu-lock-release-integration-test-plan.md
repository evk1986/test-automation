# Lock Release Integration Test Plan
## Summary
This test plan covers the integration of lock release functionality in the NetconfBatchRetryService.
## Test Cases
1. **Successful lock release**: Verify that the lock is released when a failed job is retried.
2. **Failed lock release**: Verify that the lock is not released when a job is not retried.
## Staging Setup
* Queue names: probe.commands, normalize.ingest
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status
## Pass Criteria
* The lock is released when a failed job is retried.
* The job status is updated to PENDING when the lock is released.