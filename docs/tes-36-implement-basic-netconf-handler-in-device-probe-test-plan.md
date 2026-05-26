# Summary
This test plan covers the implementation of a basic NETCONF handler in Device-Probe for Cisco IOS-XR devices.

# Test cases
1. Connect to Cisco IOS-XR device using NETCONF protocol
2. Run protocol commands and collect raw responses
3. Publish raw response to SQS queue

# Staging setup
* Queue names: probe.commands, normalize.ingest
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health

# Pass criteria
* The NETCONF handler connects to the Cisco IOS-XR device successfully
* The handler runs protocol commands and collects raw responses correctly
* The raw response is published to the SQS queue successfully