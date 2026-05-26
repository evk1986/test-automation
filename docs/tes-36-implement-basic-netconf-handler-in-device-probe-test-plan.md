# Summary
This test plan is for the implementation of a basic NETCONF handler in Device-Probe for Cisco IOS-XR devices.

# Test cases
1. Test the connection to the Cisco IOS-XR device using the NETCONF protocol.
2. Test the execution of protocol commands and collection of raw responses.
3. Test the publishing of raw responses to the SQS queue.

# Staging setup
* Queue name: probe.commands
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health

# Pass criteria
* The NETCONF handler successfully connects to the Cisco IOS-XR device.
* The NETCONF handler successfully executes protocol commands and collects raw responses.
* The NETCONF handler successfully publishes raw responses to the SQS queue.