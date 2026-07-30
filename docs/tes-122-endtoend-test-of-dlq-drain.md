# Summary
Validate that the **DLQ drain workflow** for the `probe.commands` dead‑letter queue processes a burst of poisoned messages within the defined SLA and records audit entries in the `probe.dlq_audit` Cassandra table (consistency `LOCAL_QUORUM`). The test also verifies the Prometheus metric `platform_dlq_probe_commands_seconds` reflects the processing latency.

---

## Test Cases
1. **Load DLQ** – Publish 5,000 malformed `ProbeJobMessage` payloads to the `probe.commands` DLQ.
2. **Invoke Drain Endpoint** – Call the internal `/internal/orchestrate/dlq/drain` endpoint (or invoke `DlqDrainService.drainDlq`) with `maxMessages=5000`.
3. **SLA Verification** – Ensure the total processing time is ≤ **30 seconds** (as reported by the `platform_dlq_probe_commands_seconds` histogram).
4. **Cassandra Consistency** – Query `probe.dlq_audit` and confirm **5,000** rows exist with `status=SUCCESS` and read‑consistency `LOCAL_QUORUM`.
5. **Idempotency** – Re‑run the drain with an empty DLQ and verify the service returns `0` processed messages and no new audit rows.

---

## Staging Setup
- **SQS Queues**
  - `probe.commands` (primary queue – not used in this test)
  - `probe.commands.dlq` – URL injected via `aws.sqs.probe.commands.dlq-url`
- **Cassandra Table**
  ```cql
  CREATE TABLE IF NOT EXISTS probe.dlq_audit (
      id uuid PRIMARY KEY,
      message_id text,
      processed_at timestamp,
      status text
  ) WITH default_time_to_live = 86400;
  ```
- **Actuator Endpoint**
  - `GET /actuator/metrics/platform_dlq_probe_commands_seconds` – used to fetch the latency histogram.
- **Spring Beans**
  - `DlqDrainService` wired with `SqsAsyncClient` and `DlqAuditRepository`.
- **Test Configuration**
  - `application-staging.yml` sets `aws.sqs.probe.commands.dlq-url` to the staging DLQ URL.
  - `spring.data.cassandra.consistency-level=LOCAL_QUORUM`.

---

## Pass Criteria
- All **5,000** DLQ messages are removed from the queue.
- `DlqAuditRepository` contains **5,000** records with `status=SUCCESS`.
- The Prometheus metric reports a processing duration ≤ **30 s**.
- Re‑running the drain on an empty DLQ returns `0` processed messages and does not create additional audit rows.
- No uncaught exceptions are logged during the run.
