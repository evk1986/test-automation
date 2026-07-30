# Runbook: Document NETCONF handler runbook and ADR (PRB-4821)

**Ticket:** TES-114

## Summary
## Description
Create operational documentation for the new NETCONF handler and capture architectural decisions.
## Scope
- Confluence runbook describing DLQ drain steps for probe.commands after NETCONF timeout spikes
- Update ADR‑0042 with sequence diagram showing Schema‑Normalizer → Data‑Enricher → new NETCONF handler flow
## Acceptance criteria
- Runbook published and linked to ticket PRB-4821
- ADR‑0042 diagram reflects new handler integration and is approved by architecture review

## Scope
- Verify Document NETCONF handler runbook and ADR (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
