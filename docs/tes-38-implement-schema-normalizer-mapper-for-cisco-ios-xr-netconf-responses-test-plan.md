# Test Plan: Implement Schema-Normalizer mapper for Cisco IOS-XR NETCONF responses
## Summary
This test plan covers the implementation of a new mapper in the Schema-Normalizer service to normalize raw NETCONF responses from Cisco IOS-XR devices into canonical internal DTOs.
## Test cases
1. Test the `CiscoIosXrNetconfMapper` class with a valid NETCONF response from a Cisco IOS-XR device.
2. Test the `NetconfNormalizationService` class with a valid NETCONF response from a Cisco IOS-XR device.
## Staging setup
* Queue names: `normalize.ingest`, `platform.results.dlq`
* Cassandra table: `normalized_records`
* Actuator endpoint: `/actuator/health`
## Pass criteria
* The `CiscoIosXrNetconfMapper` class correctly maps the NETCONF response to a `NormalizedRecord` object.
* The `NetconfNormalizationService` class correctly normalizes the NETCONF response using the `CiscoIosXrNetconfMapper` class.