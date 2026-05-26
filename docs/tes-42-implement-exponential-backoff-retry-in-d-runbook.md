# Runbook: Implement Exponential-Backoff Retry in Device-Probe NETCONF Worker
## Introduction
This runbook provides a step-by-step guide to implementing exponential-backoff retry in the Device-Probe NETCONF worker.

## Prerequisites
* Java 17
* Spring Boot 2.7.7
* Cassandra

## Steps
1. Modify the Device-Probe worker pool config to include the retry strategy interface.
2. Implement exponential backoff for NETCONF session timeouts on IOS-XR devices.
3. Update the retry handler to route exhausted-retry batches to the dead-letter queue with the device batch ID.

## Verification
* Verify that the retry handler successfully routes exhausted-retry batches to the dead-letter queue with the device batch ID.
* Verify that the retry handler correctly implements exponential backoff for NETCONF session timeouts on IOS-XR devices.