# Test Plan – ENR-77402 QA

**Ticket**: TES-140 – QA for Data‑Enricher SQS consumer enhancements

## Summary
Validate the new idempotency and visibility‑timeout behavior of the Data‑Enricher consumer that reads from the `enrich.pipeline` queue.

## Test Cases
1. **Duplicate Message Replay**
   * Replay the same `EnrichmentMessage` twice through the `enrich.pipeline` queue.
   * Verify that only **one** row is written to the `enrichment_result` Cassandra table for the given `normalizedRecordId`.
2. **Rapid‑Poll Batch Visibility**
   * Trigger a rapid‑poll batch using the batch identifier `POLL-RAPID-77402`.
   * Simulate a processing delay longer than the default visibility timeout (30 s).
   * Confirm that the consumer extends the visibility timeout (via SDK call) and the message is **not** re‑queued.
3. **Micrometer Metrics**
   * After processing a batch of 100 distinct messages, query the Prometheus endpoint (`/actuator/prometheus`).
   * Ensure the metric `enricher.results.written` equals the number of unique messages processed.
4. **Staging End‑to‑End**
   * Deploy the updated Data‑Enricher service to the `staging` environment.
   * Run the duplicate‑message test against the real SQS queue `enrich.pipeline`.
   * Verify no DLQ entries appear.

## Staging Setup
| Component | Identifier | Details |
|-----------|------------|---------|
| SQS Queue | `enrich.pipeline` | Standard queue, visibility timeout 30 s, DLQ `platform.results.dlq` |
| Cassandra Table | `enrichment_result` | Primary key `normalized_record_id` (text) |
| Actuator Endpoint | `http://staging-enricher.internal:8080/actuator/prometheus` | Exposes Micrometer counters |
| Metrics Scrape | Prometheus server configured to scrape the above endpoint |

## Pass Criteria
* All test cases pass without any duplicate writes.
* No message is re‑queued during the rapid‑poll run.
* The Prometheus metric `enricher.results.written` reflects the exact number of unique messages processed.
* The DLQ `platform.results.dlq` remains empty after the test suite.

---
*Prepared by the Data‑Enricher QA team on 2026‑06‑16.*
