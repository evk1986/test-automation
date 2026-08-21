# Test Plan: Validate Hazelcast SNMP lock and SQS idempotency for Device-Probe (PRB-4821)

**Ticket:** TES-195

## Summary
## Description
Run integration tests against staging probe.commands queue to ensure lock serialization and idempotent processing.
## Scope
- Deploy Device-Probe pod with new code to staging.
- Enqueue two SNMP walk messages for the same device‑id.
- Verify only one walk executes via Hazelcast lock metrics.
- Send duplicate SQS message and confirm second is skipped.
## Acceptance criteria
- Hazelcast lock metric shows count=1 for concurrent attempts.
- Duplicate message‑id does not create extra C

## Scope
- Verify Validate Hazelcast SNMP lock and SQS idempotency for Device-Probe (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
