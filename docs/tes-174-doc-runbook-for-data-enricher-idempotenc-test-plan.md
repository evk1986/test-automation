# Test Plan – Data‑Enricher Idempotency & Visibility‑Timeout

**Ticket:** TES‑174  
**Feature:** Runbook for Data‑Enricher idempotency & timeout (ENR‑77402)

## Summary
This test plan validates the operational procedures described in the runbook:
1. Idempotency key storage in Cassandra.
2. Visibility‑timeout metric collection and alerting.
3. Correct draining of the DLQ (`platform.results.dlq`) when duplicate messages are observed.

## Test Cases

1. **Idempotency Record Creation**
   - **Given** a fresh enrichment message with a unique idempotency key.
   - **When** the message is processed by `DocsRunbooksDataEnricherIdempotencyHandler`.
   - **Then** a record must exist in the `enrichment_result` table and the metric `enricher.idempotent.success` increments.

2. **Duplicate Detection**
   - **Given** an already‑processed idempotency key.
   - **When** the same message is re‑queued (simulating SQS redelivery).
   - **Then** the service logs a warning, does **not** create a new Cassandra row, and the metric `enricher.idempotent.duplicate` increments.

3. **Visibility‑Timeout Monitoring**
   - **Given** the staging environment with the queue `enrich.pipeline`.
   - **When** a message remains invisible for longer than the configured timeout (30 s).
   - **Then** the Prometheus metric `aws_sqs_visibility_timeout_seconds` should fire an alert; the runbook steps for verification must succeed.

4. **DLQ Drain Procedure**
   - **Given** the DLQ `platform.results.dlq` contains messages flagged as duplicates.
   - **When** an operator executes the TL;DR drain script (see runbook).
   - **Then** the DLQ count drops to zero and a success log entry appears in CloudWatch.

5. **Rollback Safety**
   - **Given** a failed drain attempt (e.g., network partition).
   - **When** the operator aborts the script.
   - **Then** no messages are lost; the DLQ retains its original content.

## Staging Setup
- **Queues**: `enrich.pipeline` (primary), `platform.results.dlq` (dead‑letter).
- **Cassandra Table**: `enrichment_result` with primary key `idempotency_key`.
- **Actuator Endpoint**: `http://staging-enrich.internal:8080/actuator/metrics/enricher.idempotent.*`
- **Prometheus Dashboard**: `Data‑Enricher Idempotency` (link provided in runbook).

## Pass Criteria
- All test cases execute without errors.
- Metrics appear as expected on the dashboard.
- DLQ drain completes and logs the expected success message.
- Runbook steps are reproduced verbatim by a tester.

---
*Prepared by the Data‑Enricher team on 2026‑06‑30.*