# Summary
Validate the NETCONF retry handler on the staging probe queue.

# Test cases
1. Send a NETCONF probe job to the staging probe queue.
2. Verify that the retry handler is working correctly.
3. Check for any errors in the logs.

# Staging setup
* Queue name: probe.commands
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health

# Pass criteria
* The retry handler is working correctly.
* No errors are reported in the logs.