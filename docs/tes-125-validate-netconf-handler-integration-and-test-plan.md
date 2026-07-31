# Test Plan – TES‑125 – Validate NETCONF Handler Integration and Batch Processing

## Summary
This test plan validates the end‑to‑end processing of a staging batch **BATCH-PRB-20240523-USE1-01** through the NETCONF handler. It confirms that messages flow from the `probe.commands` SQS queue, are processed by `NetconfBatchHandler`, persisted to the `probe.results` Cassandra table with `LOCAL_QUORUM`, and that the run is idempotent (no duplicate records).

## Test Cases
1. **Message Ingestion** – Publish a `ProbeJobMessage` with protocol `NETCONF` and batch ID `BATCH-PRB-20240523-USE1-01` to the `probe.commands` queue. Verify the handler receives the message (log entry or metric `netconf.batch.processed`).
2. **Cassandra Persistence** – After processing, query the `probe.results` table for rows where `batch_id = 'BATCH-PRB-20240523-USE1-01'`. Assert that the row count matches the number of devices in the batch and that each row has `status = 'SUCCESS'`.
3. **Idempotency** – Re‑publish the same message a second time. Verify that the row count in `probe.results` does **not** increase and that no duplicate `InterfaceRecord` entries appear.
4. **DLQ Metric Check** – Ensure the Prometheus metric `platform.dlq` for the `probe.commands` queue remains at `0` during the run.
5. **Failure Counter Invariance** – Confirm that the Micrometer counter `netconf.failure` does not increment for this successful execution.

## Staging Setup
- **AWS Region**: `us-east-1` (staging environment)
- **SQS Queue**: `probe.commands`
- **Cassandra Keyspace / Table**: `netatlas.probe.results` (consistency `LOCAL_QUORUM`)
- **Spring Actuator Endpoints**: `http://staging-probe.internal:8080/actuator/health` and `.../actuator/metrics`
- **Hazelcast Cluster**: default configuration; lock name pattern `netconf-batch-<batchId>`
- **Metrics**: Micrometer exported to Prometheus; verify counters `netconf.batch.processed`, `netconf.failure`, and `platform.dlq`.

## Pass Criteria
- All devices in the batch have a corresponding entry in `probe.results` with status `SUCCESS`.
- No entries appear in the DLQ metric.
- The `netconf.failure` counter remains unchanged.
- Re‑processing the same batch does not create duplicate rows.

---
*Prepared for ticket **TES‑125** on 2026‑06‑05.*