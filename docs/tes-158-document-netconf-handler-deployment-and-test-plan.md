# Test Plan – NETCONF Handler Deployment (TES-158)

## Summary
This test plan validates the deployment of the NETCONF handler introduced for ticket **PRB-4821**. The focus is on correct SQS consumption, status updates in Cassandra, and proper handling of DLQ messages.

## Test Cases
1. **Happy Path – Successful NETCONF Execution**
   - Enqueue a `ProbeJobMessage` with protocol `NETCONF` on queue `probe.commands`.
   - Verify that `DocsRunbookNetconfHandler` invokes `DocsRunbookNetconfService`.
   - Confirm the corresponding `ProbeJob` record is updated to status `SUCCESS` and `attemptCount` increments to `1`.
2. **Non‑NETCONF Message Ignored**
   - Send a message with protocol `SNMP`.
   - Ensure the handler logs a skip and does **not** modify any `ProbeJob`.
3. **Missing Job Record**
   - Publish a NETCONF message referencing a non‑existent job ID.
   - Verify that an error is logged and no Cassandra write occurs.
4. **DLQ Drain after Visibility‑Timeout Reset**
   - Simulate a visibility‑timeout expiry that moves a message to `platform.results.dlq`.
   - Run the DLQ drain script (see runbook) and confirm the message is re‑queued to `probe.commands` with a new visibility timeout of **30 seconds**.
5. **Idempotency on Retry**
   - Force a failure (throw exception inside service) and ensure the job status becomes `FAILED`.
   - Re‑process the same message and verify the status transitions to `SUCCESS` on the second attempt.

## Staging Setup
- **SQS Queues**: `probe.commands` (standard), `platform.results.dlq` (dead‑letter).
- **Cassandra Table**: `probe_job` (primary key `id`). Ensure the table contains a row for `JOB-NETCONF-4821` before test case 1.
- **Actuator Endpoint**: `http://localhost:8080/actuator/health` must return `UP` before running the handler.
- **Environment**: Deploy to `staging` environment (region `us-east-1`).

## Pass Criteria
- All test cases execute without uncaught exceptions.
- Cassandra reflects the expected status and attempt counts.
- DLQ drain script re‑queues messages and updates visibility timeout as documented.
- No stray messages remain in `platform.results.dlq` after the test suite.
