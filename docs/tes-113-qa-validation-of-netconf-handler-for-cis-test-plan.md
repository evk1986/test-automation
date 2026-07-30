# Test Plan: QA validation of NETCONF handler for Cisco IOS‑XR NCS (PRB-4821)

**Ticket:** TES-113

## Summary
## Description
Execute an end‑to‑end validation of the new NETCONF handler on the staging environment.
## Scope
- Deploy code to staging, trigger batch BATCH-PRB-20240523-USE1-01
- Verify SQS probe.commands ingestion, Cassandra probe.results write with LOCAL_QUORUM
- Check Micrometer metric increment on simulated failure
## Acceptance criteria
- All devices in batch report successful processing in logs
- probe.results rows present with expected DTO fields
- metric probe.protocol.failures updates

## Scope
- Verify QA validation of NETCONF handler for Cisco IOS‑XR NCS (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
