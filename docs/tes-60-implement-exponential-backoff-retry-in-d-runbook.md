# Runbook
This runbook outlines the steps to implement exponential-backoff retry in the Device-Probe NETCONF worker.

## Prerequisites
* The Device-Probe service is deployed and running.
* The NETCONF protocol is configured and enabled.

## Steps
1. Implement the exponential-backoff retry logic in the NETCONF worker.
2. Configure the max attempts and backoff duration.
3. Test the implementation using the test plan.

## Verification
* Verify that the NETCONF worker successfully establishes a session with exponential backoff.
* Verify that the worker routes to the dead-letter queue after max attempts.
