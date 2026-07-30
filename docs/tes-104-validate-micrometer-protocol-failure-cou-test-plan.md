# Test Plan: Validate Micrometer protocol failure counters and DLQ gauge (PRB-4821)

**Ticket:** TES-104

## Summary
## Description
Execute integration tests against the updated Device‑Probe service to confirm metric behavior.
## Scope
- Deploy dev pod with new metric code.
- Simulate NETCONF timeout and verify Counter increment.
- Inject messages into `platform.results.dlq` and verify Gauge reflects count.
- Capture metric output via `/actuator/metrics` endpoint.
## Acceptance criteria
- Counter `probe.protocol.failures{protocol="netconf"}` increments by 1 on each simulated failure.
- Gauge `platform.dlq.size

## Scope
- Verify Validate Micrometer protocol failure counters and DLQ gauge (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
