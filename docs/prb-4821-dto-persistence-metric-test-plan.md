# PRB-4821 DTO Persistence and Metric Snapshot Test Plan

## Summary
Validate that the `NetconfBatchProcessingService` correctly persists a `NormalizedRecord` DTO to Cassandra and records a Micrometer counter metric for each successful persistence. The test targets the Cisco IOS‑XR NCS batch processing path (NETCONF) and ensures end‑to‑end behavior in the PRB‑4821 context.

## Test Cases
1. **Persist and Verify Record**
   - Create a minimal `NormalizedRecord` with a unique ID, `canonicalType` set to `cisco-iosxr-ncs`, and a simple JSON payload.
   - Call `service.persistAndRecord(record)`.
   - Assert that the repository returns the record when queried by ID.
2. **Metric Counter Increment**
   - Use a `SimpleMeterRegistry` injected into the service.
   - After persisting, retrieve the counter `netconf.normalized.record.persisted` with tag `deviceFamily=cisco-iosxr-ncs`.
   - Verify that the counter value increased by **1**.
3. **Null Canonical Type Handling**
   - Persist a record without a `canonicalType`.
   - Ensure the metric is recorded with tag `deviceFamily=unknown` and no exception is thrown.

## Staging Setup
- **Queue Names**: `probe.commands` (input), `normalize.ingest` (output) – not directly used in this test but must be available in the staging profile.
- **Cassandra Table**: `normalized_record` (keyspace `netatlas_dev`). The test profile points to an embedded Cassandra instance or a dedicated test keyspace.
- **Actuator Endpoint**: `/actuator/metrics/netconf.normalized.record.persisted` – can be queried to confirm the counter after the test run.
- **Spring Profile**: `test` – loads `SimpleMeterRegistry` and an in‑memory Cassandra configuration.

## Pass Criteria
- All test cases execute without errors.
- The persisted record is retrievable from the repository and matches the input DTO.
- The Micrometer counter reflects exactly one increment per successful persistence.
- No uncaught exceptions are logged during the test execution.

---
*Generated for ticket PRB‑4821 on 2026‑06‑08.*