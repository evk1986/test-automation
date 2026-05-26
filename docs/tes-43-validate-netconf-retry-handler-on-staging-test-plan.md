# Summary
Validate NETCONF retry handler on staging probe queue.

# Test cases
1. Send a NETCONF message to the staging probe.commands queue.
2. Verify that the NETCONF retry handler is triggered.
3. Verify that the retry logic is executed correctly.

# Staging setup
* Queue name: probe.commands
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health

# Pass criteria
* The NETCONF retry handler is triggered correctly.
* The retry logic is executed correctly.
* The probe job status is updated correctly in Cassandra.