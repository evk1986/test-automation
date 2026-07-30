# Test Plan – DLQ Monitoring & Automated Drain (TES‑123)

## Summary
This test plan validates the operational runbook for monitoring the `probe.commands` dead‑letter queue (DLQ) and executing the automated drain procedure. The focus is on visibility‑timeout reset, message re‑submission, and observability metric emission.

## Test Cases
1. **Visibility Timeout Reset**
   - Publish a test message to `probe.commands`.
   - Simulate a processing failure so the message lands in `probe.commands.dlq`.
   - Execute the CLI command from the runbook to reset the visibility timeout.
   - Verify the message becomes visible again within the configured interval.
2. **Automated Drain Execution**
   - Populate the DLQ with 5 synthetic messages (include `jobId` fields).
   - Run the `dlq-drain` CLI script.
   - Confirm each message is re‑queued to the main queue and the `dlq.messages.drain.count` metric increments.
3. **Metric Validation**
   - After drain, query Prometheus for `dlq.messages.remaining` and ensure it reports `0`.
   - Verify `dlq.drain.duration` metric exists and has a non‑zero value.
4. **Error Handling**
   - Insert a malformed JSON message lacking `jobId`.
   - Run the drain command and ensure the runbook’s troubleshooting steps are followed, resulting in a warning log and the message staying in DLQ.

## Staging Setup
- **Queue Names**: `probe.commands` (main), `probe.commands.dlq` (dead‑letter).
- **Cassandra Table**: `dlq_audit` (audit of drained messages).
- **Actuator Endpoint**: `http://staging-orchestrate.internal:8080/actuator/metrics/dlq.*`
- **Metrics**: `dlq.messages.remaining`, `dlq.messages.drain.count`, `dlq.drain.duration`.

## Pass Criteria
- All test cases execute without unexpected errors.
- Prometheus metrics reflect the expected values.
- Runbook steps complete successfully and logs indicate successful re‑queue of each message.

---
*Prepared by the Orchestrate team, 2026‑07‑30.*
