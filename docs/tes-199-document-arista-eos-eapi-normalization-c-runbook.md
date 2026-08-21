# Runbook: Document Arista EOS eAPI normalization contract and enrichment flow (NORM-5510)

**Ticket:** TES-199

## Summary
## Description
Create runbook and ADR updates that describe the new SQS schema version and the hand‑off between Schema‑Normalizer and Data‑Enricher for Arista EOS data.
## Scope
- Confluence page detailing message schema fields and version bump.
- Sequence diagram showing Schema‑Normalizer → Data‑Enricher SNS fan‑out (ADR-0042).
- Add async API note to platform wiki with backward‑compatibility matrix.
## Acceptance criteria
- Documentation reviewed and approved by the architecture team.
- Diagra

## Scope
- Verify Document Arista EOS eAPI normalization contract and enrichment flow (NORM-5510)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
