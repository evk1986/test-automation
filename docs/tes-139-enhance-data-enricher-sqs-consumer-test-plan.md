# Test Plan – TES‑139 – Enhance Data‑Enricher SQS Consumer

## Summary
This test plan validates the idempotency handling, visibility‑timeout extension, and Micrometer metrics introduced by ticket **ENR‑77402**. The focus is on the `ComTelecomPipelineEnricherConsumer` SQS handler and its collaboration with `EnricherProcessingService`.

## Test Cases
1. **Duplicate Message Suppression**
   - **Given** a message with `MessageId = "msg‑dup-001"` already recorded in the idempotency table.
   - **When** the handler receives the same message again.
   - **Then** `EnricherProcessingService.process` is not invoked and no new Cassandra write occurs.
   - **Metrics**: `enricher.idempotency.failures` increments; `enricher.idempotency.success` does **not** increment.

2. **Visibility Timeout Extension**
   - **Given** a fresh message with a valid `ReceiptHandle`.
   - **When** the handler invokes the service.
   - **Then** the service calls `AmazonSQS.changeMessageVisibility` with a timeout of **300 seconds** (5 min).
   - **Metrics**: No failure metric; success metric increments.

3. **Successful Enrichment Flow**
   - **Given** a new message (no duplicate) and a functioning SQS client.
   - **When** the service processes the payload.
   - **Then** an `EnrichmentResult` is persisted, the idempotency key is stored, and the success counter increments.

4. **Failure Propagation**
   - **Given** an unexpected runtime exception during enrichment.
   - **When** the exception bubbles out of the handler.
   - **Then** the failure counter increments and the message is left for retry or DLQ handling.

## Staging Setup
| Component | Configuration |
|-----------|----------------|
| SQS Queue | `enrich.pipeline` (standard queue) |
| DLQ       | `platform.results.dlq` |
| Cassandra Table | `enrichment_result` (primary key = `normalized_record_id`) |
| Idempotency Table | `enricher_idempotency` (primary key = `message_id`) |
| Actuator Endpoint | `GET /actuator/metrics/enricher.idempotency.success` and `...failures` |
| Micrometer Tags | `protocol` (e.g., NETCONF, SNMP) and `region` (e.g., us-east-1) |

## Pass Criteria
- All unit tests in `src/test/java/**` pass (`mvn test`).
- Integration test against a localstack SQS instance shows no duplicate writes.
- Micrometer counters appear under `/actuator/metrics` with the expected tags.
- No message is re‑queued during the 5‑minute visibility window in a simulated rapid‑poll scenario.
