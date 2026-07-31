# Runbook: Document idempotency and visibility‑timeout contract for Data‑Enricher SQS consumer (ENR-77402)

**Ticket:** TES-129

## Summary
## Description
Create an Architecture Decision Record and update the runbook to capture the new idempotency and visibility‑timeout behavior.
## Scope
- ADR‑0045 describing design rationale, trade‑offs, and metric tags.
- Confluence runbook section detailing steps to reset visibility timeout during vendor‑wide timeout events.
- Add version bump note to async API contract matrix.
## Acceptance criteria
- ADR published and linked from platform wiki.
- Runbook reviewed and approved by the Data‑Enric

## Scope
- Verify Document idempotency and visibility‑timeout contract for Data‑Enricher SQS consumer (ENR-77402)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
