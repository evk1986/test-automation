# Test Plan – TES‑161 – Validate Arista EOS Mapper and Enrichment Flow (NORM‑5510)

## Summary
This test plan validates that the updated **Schema‑Normalizer** mapper for Arista EOS devices creates correct `NormalizedRecord` entries and that the **Data‑Enricher** processes those entries without creating duplicate rows in Cassandra.

## Test Cases
1. **Deploy to Staging**
   - Deploy the latest `schema‑normalizer` image to the `staging` environment.
   - Verify the service starts and registers with Consul (`service/schema‑normalizer`).
2. **Inject Sample Messages**
   - Send three sample `ProbeJobMessage` payloads (representing devices 7050‑S, 7280‑R, 7050‑S) to the `probe.commands` queue with `batchId = BATCH-PRB-20240523-USE1-01`.
   - Confirm that `normalize.ingest` receives three `NormalizedRecord` messages with `canonicalType = "AristaEos"`.
3. **Validate Cassandra Records**
   - Query the `device_results` table in the `staging` Cassandra keyspace.
   - Expect three rows with `canonical_type = 'AristaEos'` and populated `enriched_fields`.
4. **Idempotency Replay**
   - Re‑publish the same three SQS messages to `probe.commands`.
   - Verify that the `device_results` table still contains exactly three rows (no duplicates).
5. **Metrics Check**
   - Pull Prometheus metrics from `http://staging‑service‑monitor:9090/metrics`.
   - Ensure `probe_protocol_failures_total` does **not** increase during the replay.

## Staging Setup
- **Queues**
  - `probe.commands` – source of raw probe jobs.
  - `normalize.ingest` – target for normalized records.
  - `enrich.pipeline` – fan‑out for enriched results (SNS topic ARN stored in Vault under `secret/data/enrich/topic`).
- **Cassandra**
  - Keyspace: `netatlas_staging`
  - Table: `device_results` (primary key `id`).
- **Actuator Endpoints**
  - `http://staging‑schema‑normalizer:8080/actuator/health`
  - `http://staging‑data‑enricher:8080/actuator/metrics`

## Pass Criteria
- All three `NormalizedRecord` entries exist with correct `deviceId`, `protocol = "EAPI"`, and non‑null `normalizedPayload`.
- Exactly three rows in `device_results` after the replay step.
- The metric `probe_protocol_failures_total` remains unchanged (Δ = 0).
- No error entries appear in the DLQ (`platform.results.dlq`).
