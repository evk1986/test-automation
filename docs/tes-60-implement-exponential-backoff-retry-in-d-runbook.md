# Runbook: Implement Exponential-Backoff Retry in Device-Probe NETCONF Worker
## Introduction
This runbook provides steps to implement exponential-backoff retry in the Device-Probe NETCONF worker.
## Prerequisites
* The Device-Probe service is deployed and running.
* The NETCONF protocol is configured and working correctly.
## Steps
1. Implement the exponential-backoff retry logic in the NetconfBatchRetryService class.
2. Configure the retry policy in the application configuration file.
3. Test the retry mechanism using the test plan provided.