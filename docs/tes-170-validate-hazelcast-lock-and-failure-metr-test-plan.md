# Test Plan: Validate Hazelcast lock and failure metrics for Device-Probe (PRB-4821 QA)

**Ticket:** TES-170

## Summary
## Description
Execute functional and metric verification for the new lock and Micrometer counter.
## Scope
- Simulate concurrent SNMP walks against dev Device-Probe cluster.
- Query /actuator/metrics for probe.protocol.failures.
## Acceptance criteria
- Test confirms only one SNMP walk per device-id per batch.
- Counter reports failures with correct tags for NETCONF, SNMP, and region us-east-1.

## Scope
- Verify Validate Hazelcast lock and failure metrics for Device-Probe (PRB-4821 QA)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
