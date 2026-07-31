# Edge Cases: QA for Data-Enricher SQS consumer enhancements (ENR-77402 QA)

**Ticket:** TES-140

## Edge Cases
## Description
Validate the new idempotency and visibility‑timeout behavior of the Data‑Enricher consumer.
## Scope
- Replay duplicate SQS messages through the enrich.pipeline queue.
- Verify only one Cassandra write per device‑id.
- Stress test with rapid‑poll batch POLL-RAPID-77402 to ensure timeout extension prevents re‑processing.
- Confirm Micrometer metrics are emitted correctly.
## Acceptan

## Validation
- QA for Data-Enricher SQS consumer enhancements (ENR-77402 QA)
- Environment: staging Device-Probe / Data-Enricher cluster
- Linked to TES-140
