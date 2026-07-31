# Test Plan – Add Hazelcast Distributed Lock for SNMP Walk Concurrency (TES‑130)

## Summary
This test plan validates the distributed locking implementation introduced for SNMP walk execution in the **Device‑Probe** service. The lock key follows the schema `deviceId|batchId`. The plan covers unit‑level behavior, integration against a staging Hazelcast cluster, and metric verification.

## Test Cases
1. **Lock Acquisition Success**
   - **Given** a `ProbeJobMessage` for device `device-123` in batch `BATCH-PRB-20240523-USE1-01`.
   - **When** the service attempts to acquire the lock.
   - **Then** the lock is obtained, `snmp.lock.acquired` counter increments, SNMP walk simulation runs, and `snmp.lock.released` counter increments.

2. **Lock Acquisition Timeout**
   - **Given** another worker already holds the lock for the same `deviceId|batchId`.
   - **When** a second request tries to acquire the lock.
   - **Then** acquisition fails after 5 seconds, no counters are incremented, and the job is not processed.

3. **Lock Key Schema Validation**
   - **Given** any incoming message.
   - **When** the lock is requested.
   - **Then** the key passed to Hazelcast matches `<deviceId>|<batchId>` exactly.

4. **Metric Emission**
   - **Given** a successful lock acquisition and release.
   - **When** the operation completes.
   - **Then** Prometheus scrapes `snmp_lock_acquired_total` and `snmp_lock_released_total` with non‑zero values.

5. **Integration with Staging Batch**
   - **Given** batch `BATCH-PRB-20240523-USE1-01` loaded in the staging environment.
   - **When** the full pipeline runs (SQS → handler → service).
   - **Then** no two SNMP walks for the same device run concurrently; logs contain lock acquisition/release messages.

## Staging Setup
- **SQS Queue**: `probe.commands`
- **Hazelcast Cluster**: Accessible via DNS `hazelcast-staging.internal:5701`
- **Cassandra Table**: `device_snapshot` (used by downstream services – not touched by this change)
- **Actuator Endpoint**: `http://probe-staging.internal:8080/actuator/metrics`
- **Prometheus Scrape**: Verify `snmp.lock.acquired` and `snmp.lock.released` metrics.

## Pass Criteria
- All unit tests in `HazelcastLockBeanUpdateSnmpServiceTest` pass with ≥ 80 % coverage.
- Integration test suite runs against the staging batch without lock contention errors.
- Prometheus shows both counters incremented at least once after a successful run.
- No duplicate SNMP walk executions are observed for any device within the same batch.
