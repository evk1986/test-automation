# Test Plan for NETCONF Handler for Cisco IOS-XR NCS Devices
## Summary
This test plan covers the testing of the NETCONF handler for Cisco IOS-XR NCS devices.
## Test Cases
1. Test the handleNetconfMessage method with a valid NETCONF message for Cisco IOS-XR NCS devices.
2. Test the handleNetconfMessage method with an invalid NETCONF message for Cisco IOS-XR NCS devices.
## Staging Setup
* Queue names: probe.commands, enrich.pipeline
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health
## Pass Criteria
* The handleNetconfMessage method processes the NETCONF message correctly for Cisco IOS-XR NCS devices.
* The handleNetconfMessage method handles invalid NETCONF messages correctly for Cisco IOS-XR NCS devices.