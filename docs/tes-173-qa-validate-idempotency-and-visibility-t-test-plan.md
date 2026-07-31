# Test Plan – TES‑173 – Validate Idempotency and Visibility Timeout for Data‑Enricher (ENR‑77402)

## Summary
This test plan validates that the **Data‑Enricher** service correctly handles duplicate SQS messages and extends the visibility timeout for long‑running enrichment tasks.

## Test Cases
1. **Replay Batch** – Replay the batch `BATCH-PRB-20240523-USE1-01` through the full Data‑Enricher pipeline.
2. **Inject Duplicate Messages** – Publish two SQS messages with the same `messageId` (`msg‑dup‑001`) but different receipt handles to the `enrich.pipeline` queue.
3. **Verify Single Write** – Query the `enrichment_result` table in Cassandra and assert that only **one** row exists for `msg‑dup‑001`.
4. **Check Visibility Timeout Extension** –
   - Retrieve the `ApproximateReceiveCount` and `VisibilityTimeout` attributes for each receipt handle via the AWS SDK.
   - Confirm that after processing, the visibility timeout is **≥ 300 seconds** for both messages.
5. **Metrics Validation** – Call `/actuator/metrics/enricher.visibility.timeout` and verify that the metric `enricher.visibility.timeout` records a value of **300** (or higher) for the processed messages.

## Staging Setup
- **Queue**: `enrich.pipeline` (standard SQS, DLQ = `platform.results.dlq`).
- **Cassandra Table**: `enrichment_result` with primary key `message_id`.
- **Actuator Endpoint**: `http://staging-enrich.internal.netatlas.com/actuator/metrics/enricher.visibility.timeout`.
- **AWS Region**: `us-east-1` (environment `staging`).
- **Test Harness**: JUnit 5 integration test suite located under `src/integration-test/java/...`.

## Pass Criteria
- The idempotency key (`messageId`) prevents a second write; the row count for the duplicate remains **1**.
- Visibility timeout for each processed receipt is **≥ 300 seconds**.
- Metric `enricher.visibility.timeout` reports a value of **300** (or higher) for the test run.
- No errors are recorded in the service logs for the duplicate handling path.

---
*Prepared by the Data‑Enricher team on 2026‑06‑26.*