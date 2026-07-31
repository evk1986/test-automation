# Test Plan: Validate NETCONF handler for Cisco IOS‑XR NCS batch (PRB-4821)

**Ticket:** TES-167

## Summary
## Description
Execute regression and integration tests to ensure the new NETCONF handler processes devices correctly and respects concurrency controls.
## Scope
- Run full Device‑Probe regression suite on staging against batch BATCH-PRB-20240523-USE1-01.
- Replay probe.commands messages with simulated NETCONF timeouts.
- Verify Hazelcast lock prevents duplicate walks.
## Acceptance criteria
- All regression tests pass without new failures.
- Timeout scenarios are recorded in probe.protocol.fail

## Scope
- Verify Validate NETCONF handler for Cisco IOS‑XR NCS batch (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
