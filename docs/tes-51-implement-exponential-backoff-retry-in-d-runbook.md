# Runbook for Implementing Exponential Backoff Retry in Device-Probe NETCONF Worker
## Introduction
This runbook provides a step-by-step guide for implementing exponential backoff retry in the Device-Probe NETCONF worker.
## Prerequisites
* The Device-Probe worker is configured to use the NETCONF protocol.
* The IOS-XR device family is supported.
## Steps
1. Update the Device-Probe worker pool configuration to include the exponential backoff retry policy.
2. Implement the exponential backoff retry logic in the NetconfRetryService class.
3. Test the implementation using the test plan provided.