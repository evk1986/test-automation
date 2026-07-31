# Runbook: Document Hazelcast lock usage and metrics in Device-Probe runbook (PRB-4821 Docs)

**Ticket:** TES-171

## Summary
## Description
Create runbook and ADR detailing lock configuration, troubleshooting, and metric interpretation.
## Scope
- Confluence page with lock setup steps, lock‑acquire/release flow, and DLQ considerations.
- Include sequence diagram linking Device-Probe lock acquisition to probe.commands processing.
- Add note on Micrometer counter usage in platform monitoring.
## Acceptance criteria
- Runbook reviewed and approved by the Device‑Probe team.
- Diagram published in ADR‑0042.

## Scope
- Verify Document Hazelcast lock usage and metrics in Device-Probe runbook (PRB-4821 Docs)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
