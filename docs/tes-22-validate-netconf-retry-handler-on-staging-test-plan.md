# Summary
This test plan is designed to validate the NETCONF retry handler on the staging probe queue.

# Test cases
1. Send a NETCONF probe job to the staging probe queue.
2. Verify that the NETCONF retry handler is triggered and the job is retried.
3. Verify that the job status is updated to "RUNNING".

# Staging setup
* Queue name: probe.commands
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health

# Pass criteria
* The NETCONF retry handler is triggered and the job is retried.
* The job status is updated to "RUNNING".