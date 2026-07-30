# Test Plan: QA: Verify Hazelcast lock and timeout for SNMP walks (PRB-5001)

**Ticket:** TES-101

## Summary
## Description
Execute integration tests to validate the Hazelcast lock and visibility timeout behavior for SNMP walks.
## Scope
- Deploy Device-Probe to dev cluster.
- Simulate concurrent SNMP walk requests for device-id 10.0.0.5 in batch BATCH-PRB-20240523-USE1-01.
- Verify only one SNMP request sent and lock contention metric increments.
- Confirm no duplicate SQS messages processed.
## Acceptance criteria
- Test suite passes with lock serialization confirmed.
- /actuator/metrics/snmp.lock.co

## Scope
- Verify QA: Verify Hazelcast lock and timeout for SNMP walks (PRB-5001)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
