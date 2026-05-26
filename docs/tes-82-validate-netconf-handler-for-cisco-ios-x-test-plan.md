# Summary
This test plan validates the implementation of the NETCONF handler for Cisco IOS-XR NCS devices.

# Test cases
1. Test handler with sample devices and batches
2. Verify handler collects and processes NETCONF responses correctly

# Staging setup
* Queue names: probe.commands
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status

# Pass criteria
* Handler connects to Cisco IOS-XR NCS devices successfully
* Handler collects and processes NETCONF responses correctly