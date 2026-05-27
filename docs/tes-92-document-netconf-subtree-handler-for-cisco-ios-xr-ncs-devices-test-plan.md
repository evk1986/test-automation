# Test Plan: Document NETCONF Subtree Handler for Cisco IOS-XR NCS Devices
## Summary
This test plan verifies the functionality of the NETCONF subtree handler for Cisco IOS-XR NCS devices.
## Test Cases
1. Test the handleNetconfSubtree method with a valid ProbeJobMessage.
2. Test the handleNetconfSubtree method with an invalid ProbeJobMessage.
## Staging Setup
* Queue names: probe.commands, platform.results.dlq
* Cassandra table: probe_jobs
* Actuator endpoint: /api/v1/probe/jobs/{jobId}/status
## Pass Criteria
* The handleNetconfSubtree method processes the ProbeJobMessage correctly.
* The SQS message schema version is updated correctly.