# Test Plan: Validate NETCONF NCS circuit breaker behavior (PRB-4821)

**Ticket:** TES-107

## Summary
## Description
Execute integration tests that simulate NETCONF timeouts and failures to verify circuit breaker state transitions and DLQ routing.
## Scope
- Mock NETCONF server inducing timeouts
- Verify retry count and backoff intervals
- Confirm messages land in platform.results.dlq after max attempts
- Check Micrometer latency metric emission
## Acceptance criteria
- Tests pass for open, half‑open, and open states
- DLQ contains expected poisoned message IDs
- Metrics appear with correct tags

## Scope
- Verify Validate NETCONF NCS circuit breaker behavior (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
