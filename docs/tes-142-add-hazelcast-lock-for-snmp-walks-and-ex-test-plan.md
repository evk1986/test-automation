# Test Plan – TES‑142 – Add Hazelcast Lock for SNMP Walks and Expose Protocol Failure Metrics

## Summary
This test plan validates the implementation of a Hazelcast distributed lock that serialises SNMP walk executions per device‑id per batch and the Micrometer counter that records protocol‑specific failure rates. The changes are scoped to the **Device‑Probe** service.

## Test Cases
1. **Lock Acquisition Success**
   - **Given** a fresh batch `BATCH-PRB-20240523-USE1-01` and device `device‑001`.
   - **When** the handler invokes `acquireLock`.
   - **Then** the lock is obtained and the method returns `true`.

2. **Lock Contention**
   - **Given** the lock for `device‑002`/`BATCH-PRB-20240523-USE1-01` is held by a worker.
   - **When** a second worker attempts to acquire the same lock.
   - **Then** `acquireLock` returns `false` and the second worker does not perform the SNMP walk.

3. **Lock Release**
   - **Given** a lock held by the current thread.
   - **When** `releaseLock` is called.
   - **Then** the lock is released and subsequent acquisition succeeds.

4. **Failure Counter Increment**
   - **Given** a simulated SNMP walk that fails (device hash % 5 == 0).
   - **When** `recordFailure("SNMP", "us-east-1")` is invoked.
   - **Then** the Micrometer counter `probe.protocol.failures` with tags `protocol=SNMP` and `region=us-east-1` increments by 1.

5. **Metrics Exposure**
   - **Given** a running instance in the `dev` environment.
   - **When** accessing `GET /actuator/metrics/probe.protocol.failures`.
   - **Then** the response contains the counter value and the associated tags.

## Staging Setup
- **SQS Queue**: `probe.commands` (standard queue, visibility timeout 30 s).
- **Cassandra Table**: `device_snapshot` (used by other services; not directly touched by this change).
- **Actuator Endpoint**: `/actuator/metrics/probe.protocol.failures` (exposed via Spring Boot Actuator).
- **Hazelcast Cluster**: single‑node dev cluster reachable at `hazelcast://localhost:5701`.
- **Micrometer Registry**: `SimpleMeterRegistry` in unit tests; `PrometheusMeterRegistry` in staging.

## Pass Criteria
- All test cases execute without failures.
- Concurrent SNMP walk attempts for the same device‑id are serialized (only one succeeds per batch).
- The failure counter reflects the exact number of simulated errors.
- The metric is visible and correctly tagged at the actuator endpoint.

---
*Prepared by the junior backend contractor for ticket PRB‑874 (TES‑142).*