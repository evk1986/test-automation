# Test Plan: Validate Arista EOS eAPI normalization and downstream enrichment (NORM-5510)

**Ticket:** TES-198

## Summary
## Description
Execute end‑to‑end integration tests on the staging environment to verify that Arista EOS eAPI messages are normalized and correctly enriched.
## Scope
- Deploy batch BATCH-PRB-20240523-USE1-01 to staging.
- Consume from normalize.ingest, check InterfaceRecord written to Cassandra (LOCAL_QUORUM).
- Confirm SNS fan‑out to enrich.pipeline and successful write to probe.results table.
## Acceptance criteria
- All records appear in Cassandra with expected fields.
- Enrichment metrics i

## Scope
- Verify Validate Arista EOS eAPI normalization and downstream enrichment (NORM-5510)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
