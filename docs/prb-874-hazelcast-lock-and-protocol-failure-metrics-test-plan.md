# PRB-874 QA Test Plan

## Summary
This test validates two concerns for the **Device‑Probe** service:
1. **Hazelcast lock serialization** – already covered by existing lock‑QA tests.
2. **Protocol failure metrics** – a new counter `probe.protocol.failures` must be incremented when a protocol error occurs and exposed via the Actuator endpoint.

The focus of this plan is the metric verification described in the ticket title.

## Test Cases
1. **Initial metric snapshot** – Query the Actuator endpoint `/actuator/metrics/probe.protocol.failures` for a specific protocol (e.g., `SNMP`) and record the current counter value.
2. **Induce protocol failure** – Call `ProtocolFailureMetricsService.recordFailure("SNMP")` which simulates a failure in the probe workflow.
3. **Post‑failure metric check** – Query the same Actuator endpoint again and assert that the counter increased by exactly **1**.
4. **Multiple failures** – Repeat step 2 three more times and verify the counter increments cumulatively.
5. **Isolation** – Run the test in a fresh Spring context to ensure no residual state from previous runs.

## Staging Setup
- **Queue names**: `probe.commands` (used by other tests, not touched here).
- **Cassandra tables**: No table interaction required for this metric test.
- **Actuator endpoint**: Ensure `management.endpoints.web.exposure.include=health,info,metrics` is set in `application‑staging.yml`.
- **Metrics registry**: Micrometer backed by SimpleMeterRegistry (default for tests).

## Pass Criteria
- The counter value returned by the Actuator endpoint after step 2 equals the initial value + 1.
- After step 4 the counter equals the initial value + 4.
- No exceptions are thrown during metric collection or service invocation.

---
*Generated for ticket **PRB-874** on **2026‑06‑27**.*