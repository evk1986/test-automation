# Test Plan – TES‑157 – Validate NETCONF handler for Cisco IOS‑XR NCS batch processing

## Summary
This test plan validates the end‑to‑end flow of the **TestIntegrationNetconf** handler when a batch of Cisco IOS‑XR NCS devices is processed. The flow exercised includes:
1. Enqueueing a batch identifier (`BATCH‑PRB‑20240523‑USE1‑01`) via Fleet‑Orchestrator.
2. Consuming `probe.commands` SQS messages.
3. Persisting raw NETCONF payloads to the `device_snapshot` table.
4. Publishing a notification to the `normalize.ingest` SNS topic.
5. Verifying that canonical `InterfaceRecord` DTOs appear in the `device_results` Cassandra table without duplication.

## Test Cases
| # | Description | Steps | Expected Result |
|---|-------------|-------|-----------------|
| 1 | Deploy Device‑Probe to **dev** environment | Deploy the latest `device-probe` Docker image to the `dev` namespace. | Service starts, health endpoint `/actuator/health` returns **UP**. |
| 2 | Trigger batch processing | Use the Fleet‑Orchestrator REST endpoint `POST /api/v1/orchestrator/batches` with payload `{ "batchId": "BATCH‑PRB‑20240523‑USE1‑01" }`. | Batch status becomes **RUNNING** and `probe.commands` depth increases. |
| 3 | Verify SQS message ingestion | Poll `probe.commands` queue (AWS CLI `aws sqs receive-message`). | All NCS devices in the batch produce a `ProbeJobMessage` with `protocol = NETCONF`. |
| 4 | Confirm snapshot persistence | Query Cassandra table `device_snapshot` for `job_id = 'BATCH‑PRB‑20240523‑USE1‑01'`. | Row count equals the number of NCS devices; `raw_payload` is non‑null. |
| 5 | Validate SNS fan‑out | Subscribe a test SQS queue to the `normalize.ingest` SNS topic and inspect messages. | One message per device, containing the snapshot ID. |
| 6 | Check canonical DTO creation | Query `device_results` table for `canonical_type = 'InterfaceRecord'` and `batch_id = 'BATCH‑PRB‑20240523‑USE1‑01'`. | All devices have a corresponding `InterfaceRecord` row; no duplicate `id` values. |
| 7 | Idempotency check | Re‑run the same batch without resetting the queue. | No additional rows are created; duplicate detection logs are absent. |
| 8 | Metrics verification | Retrieve Prometheus metrics `probe_protocol_failures{protocol="NETCONF"}`. | Counter value remains unchanged from baseline (no new failures). |

## Staging Setup
- **AWS SQS Queues**: `probe.commands`, `normalize.ingest` (dev environment).
- **Cassandra Keyspace**: `netatlas_dev` with tables `device_snapshot` and `device_results`.
- **Actuator Endpoint**: `http://device-probe-dev:8080/actuator/metrics` (Prometheus format).
- **Fleet‑Orchestrator**: Accessible at `http://orchestrator-dev:8080/api/v1/orchestrator`.
- **Test SNS Subscription**: Temporary SQS queue `test-normalize-ingest` subscribed to `normalize.ingest`.

## Pass Criteria
- All NCS devices in the batch generate exactly one `InterfaceRecord` DTO.
- No duplicate snapshot IDs or result IDs are observed.
- The `probe.protocol.failures` metric for NETCONF does **not** increase.
- The handler logs contain `INFO` entries confirming successful processing of each message.
- Unit and integration test suites complete with **0** failures.
