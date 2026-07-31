# Test Plan – NETCONF Handler Deployment (TES-126)

## Summary
This test plan validates the deployment, runtime behavior, and error handling of the new NETCONF handler introduced for Device‑Probe. It ensures that messages from the `probe.commands` queue are processed, results are published, and DLQ visibility timeout can be reset.

## Test Cases
1. **Happy Path Processing**
   - Send a `NetconfJobMessage` to `probe.commands`.
   - Verify `NetconfHandlerService.process` is invoked.
   - Confirm no error is logged and message is removed from the queue.

2. **Protocol Failure Handling**
   - Simulate an exception inside `NetconfHandlerService.process`.
   - Verify the exception is caught and logged.
   - Ensure the job status would be marked FAILED (implicit) and message moves to DLQ.

3. **DLQ Visibility Timeout Reset**
   - Place a failed message in `platform.results.dlq`.
   - Run the visibility‑timeout reset script (documented in runbook).
   - Confirm the message becomes visible after the configured interval.

4. **Rollback Procedure**
   - Deploy the previous version of the handler.
   - Verify that new messages are processed by the old handler without errors.
   - Confirm no residual state interferes with the rollback.

## Staging Setup
- **SQS Queues**
  - `probe.commands` (standard)
  - `platform.results.dlq` (dead‑letter queue)
- **Cassandra Table**
  - `probe_job` (stores `ProbeJob` status updates)
- **Actuator Endpoint**
  - `http://staging-probe.internal:8080/actuator/health`
- **SNS Topic**
  - `arn:aws:sns:us-east-1:123456789012:device-probe-results`

## Pass Criteria
- All test cases execute without uncaught exceptions.
- Successful processing updates `ProbeJob` status to `SUCCESS`.
- Failed processing results in a message visible in the DLQ.
- Visibility timeout reset makes the message re‑processable within 5 minutes.
- Rollback restores previous behavior with zero impact on pending jobs.
