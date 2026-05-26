# Test Plan
## Summary
This test plan covers the implementation of exponential-backoff retry in the Device-Probe NETCONF worker.
## Test Cases
1. **Successful processing**: Verify that the handler processes the message successfully and does not route to the dead-letter queue.
2. **NETCONF session timeout**: Simulate a NETCONF session timeout and verify that the handler retries with exponential backoff.
3. **Exhausted retry**: Verify that the handler routes to the dead-letter queue after exhausting the retry attempts.
## Staging Setup
* Create a test SQS queue for the probe commands.
* Configure the Device-Probe worker to use the test queue.
* Create a test dead-letter queue for the exhausted retry messages.
## Pass Criteria
* The handler processes the message successfully and does not route to the dead-letter queue.
* The handler retries with exponential backoff after a NETCONF session timeout.
* The handler routes to the dead-letter queue after exhausting the retry attempts.