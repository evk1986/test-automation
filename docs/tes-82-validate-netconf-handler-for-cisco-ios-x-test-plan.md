# Test Plan: Validate NETCONF Handler for Cisco IOS-XR NCS Devices
## Summary
This test plan validates the implementation of the NETCONF handler for Cisco IOS-XR NCS devices.
## Test Cases
1. Test handler with sample devices and batches
2. Verify handler collects and processes NETCONF responses correctly
## Staging Setup
* Queue names: probe.commands, normalize.ingest
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status
## Pass Criteria
* Handler connects to Cisco IOS-XR NCS devices successfully
* Handler collects and processes NETCONF responses correctly