# Runbook: Doc: Runbook & ADR for SNMP walk lock mechanism (PRB-5001)

**Ticket:** TES-102

## Summary
## Description
Create runbook and ADR documenting the new SNMP walk lock mechanism.
## Scope
- Confluence page outlining lock acquisition, release, and error handling.
- Steps to reset visibility timeout after lock acquisition failures.
- Sequence diagram showing Device-Probe → probe.commands flow with lock.
- Update ADR-0045 with rationale and trade‑offs.
## Acceptance criteria
- Runbook published and linked from platform wiki.
- ADR added with review sign‑off.
- Diagram included and referenced

## Scope
- Verify Doc: Runbook & ADR for SNMP walk lock mechanism (PRB-5001)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
