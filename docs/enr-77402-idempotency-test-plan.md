# ENR-77402 – Idempotency & Visibility‑Timeout Test Plan

## Summary
This test plan validates the new idempotency handling and visibility‑timeout extension added to the **Data‑Enricher** SQS consumer (`ComTelecomPipelineEnricherConsumer`). The objectives are:
1. Ensure that a message with a previously‑seen `idempotencyKey` is ignored and does not trigger duplicate enrichment work.
2. Verify that the consumer successfully extends the message visibility timeout while processing, preventing premature redelivery.
3. Confirm that successful processing results in message deletion from the `enrich.pipeline` queue.

## Test Cases
| # | Description | Expected Result |
|---|-------------|-----------------|
| 1 | Send a single well‑formed enrichment message. Verify that `DataEnricherService.enrichWithIdempotency` is called once and the message is deleted. | Enrichment succeeds, message disappears from the queue. |
| 2 | Send the same message a second time (identical `idempotencyKey`). Verify that the service returns a duplicate placeholder and `actualEnrich` is **not** invoked. | No duplicate enrichment work; log shows *Skipping enrichment*.
| 3 | Simulate a long‑running enrichment (sleep 45 s) while the original SQS visibility timeout is 30 s. Verify that the consumer extends the timeout to at least 300 s (default) before completion. | Message remains invisible for the duration of processing; no premature redelivery. |
| 4 | Force an exception inside `enrichWithIdempotency`. Verify that the message is **not** deleted and becomes visible again after the original timeout. | Message stays in the queue for retry; error is logged. |

## Staging Setup
* **Queue**: `enrich.pipeline` (standard SQS) – configured in `application‑staging.yml`.
* **Cassandra Table**: `enrichment_result` – stores `EnrichmentResult` rows (already present).
* **Actuator Endpoint**: `http://staging‑enrich.internal:8080/actuator/health` – health check for the service.
* **Environment Variables**:
  * `AWS_REGION=us-east-1`
  * `AWS_SQS_VISIBILITY_EXTENSION_SECONDS=300`
* **Dependencies**: Spring Cloud AWS, Hazelcast (for distributed idempotency in production – not required for this test).

## Pass Criteria
* All test cases execute without errors.
* No duplicate rows appear in `enrichment_result` for the same `recordId`/`idempotencyKey` pair.
* CloudWatch logs contain entries for visibility‑timeout extensions and duplicate‑skip notices.
* The SQS queue depth returns to zero after successful processing of the test messages.

---
*Prepared for ticket **ENR-77402** on 2026‑06‑15.*
