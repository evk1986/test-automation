# Runbook
## Introduction
This runbook covers the implementation of exponential-backoff retry in the Device-Probe NETCONF worker.
## Prerequisites
* The Device-Probe worker is configured to use the test SQS queue.
* The test dead-letter queue is created.
## Steps
1. **Deploy the updated Device-Probe worker**: Deploy the updated Device-Probe worker with the exponential-backoff retry implementation.
2. **Verify the handler**: Verify that the handler processes the message successfully and does not route to the dead-letter queue.
3. **Simulate a NETCONF session timeout**: Simulate a NETCONF session timeout and verify that the handler retries with exponential backoff.
4. **Verify the dead-letter queue**: Verify that the handler routes to the dead-letter queue after exhausting the retry attempts.