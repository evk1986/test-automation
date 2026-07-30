# Test Plan: QA: Verify Hazelcast lock and idempotency for NETCONF batch BATCH-PRB-20240523-USE1-01 (PRB-4821)

**Ticket:** TES-119

## Summary
## Description
Validate that the new concurrency guard and idempotent processing work correctly under realistic load.
## Scope
- Run a staged load test using batch BATCH-PRB-20240523-USE1-01.
- Inspect logs and metrics to ensure a single NETCONF session per device.
- Confirm duplicate SQS messages are skipped and reported via metrics.
## Acceptance criteria
- No device shows more than one active NETCONF session in the test run.
- Idempotent skip counter increments for injected duplicate messages

## Scope
- Verify QA: Verify Hazelcast lock and idempotency for NETCONF batch BATCH-PRB-20240523-USE1-01 (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
