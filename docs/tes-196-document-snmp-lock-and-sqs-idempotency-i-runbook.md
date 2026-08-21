# Runbook: Document SNMP lock and SQS idempotency implementation for Device-Probe (PRB-4821)

**Ticket:** TES-196

## Summary
## Description
Create Confluence runbook and ADR detailing the new lock and idempotency mechanisms.
## Scope
- Update runbook with steps to verify lock health via /actuator/metrics/hazelcast.locks.
- Add ADR‑0045 describing design decision, trade‑offs, and backward‑compatibility.
- Include troubleshooting guide for stale locks and duplicate message scenarios.
## Acceptance criteria
- Runbook published and linked to PRB-4821.
- ADR reviewed and approved.
- Team can reproduce lock verification ste

## Scope
- Verify Document SNMP lock and SQS idempotency implementation for Device-Probe (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
