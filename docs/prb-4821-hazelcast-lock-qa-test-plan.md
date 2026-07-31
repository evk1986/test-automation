# PRB-4821 QA Test Plan – Hazelcast Lock Validation for SNMP Walks

**Ticket**: PRB-4821  
**Related tickets**: TES-131  
**Date**: 2026‑06‑23  
**Author**: QA Engineer (internal)

---

## Summary
The purpose of this test plan is to verify that the distributed Hazelcast lock prevents concurrent SNMP walk executions for the same device, eliminates duplicate `DeviceSnapshot` rows in the `probe.results` Cassandra table, and guarantees lock release (map entry removal) after processing completes. The test exercises the `SnmpWalkLockService.processSnmpWalk` method, which is the entry point for the SNMP walk SQS handler.

---

## Test Cases
| # | Description | Preconditions | Steps | Expected Result |
|---|-------------|----------------|-------|-----------------|
| 1 | **Happy‑path lock acquisition** | Hazelcast cluster reachable; `probe.results` table empty for `device-123`. | 1. Publish a `ProbeJobMessage` for `device-123` to `probe.commands`. 2. Invoke `SnmpWalkLockService.processSnmpWalk`. | - Lock entry created in Hazelcast map `snmp-walk-locks`. - One `DeviceSnapshot` row persisted with correct deviceId, protocol, and timestamp. - Lock entry removed after method returns. |
| 2 | **Concurrent execution prevention** | Same as case 1, but two threads invoke the service simultaneously for `device-123`. | 1. Start Thread‑A calling `processSnmpWalk`. 2. While Thread‑A holds the lock, start Thread‑B calling `processSnmpWalk`. | - Thread‑A acquires lock, persists snapshot, releases lock. - Thread‑B fails to acquire lock, throws `IllegalStateException`. - Only one snapshot row exists for the device. |
| 3 | **Lock timeout handling** | Hazelcast map is deliberately blocked (mocked) so `tryLock` returns `false`. | 1. Invoke `processSnmpWalk` for `device-999`. | - Service throws `IllegalStateException` with message *"SNMP walk lock not acquired"*. - No snapshot persisted. - No lock entry remains in the map. |
| 4 | **Lock release on interruption** | Thread is interrupted while waiting for the lock. | 1. Call `processSnmpWalk` and interrupt the thread during `tryLock`. | - Service re‑sets the interrupt flag and throws `IllegalStateException` with cause `InterruptedException`. - No snapshot persisted. - No stale lock entry left in Hazelcast. |

---

## Staging Setup
| Component | Configuration |
|-----------|----------------|
| **SQS Queue** | `probe.commands` (standard queue) – ensure DLQ `platform.results.dlq` is attached. |
| **Cassandra Table** | Keyspace `netatlas`; table `probe_results` (mapped to `DeviceSnapshot`). Primary key: `(device_id, job_id)`. |
| **Hazelcast Map** | Distributed map name `snmp-walk-locks`. Entries are `<deviceId, lockOwner>` strings. |
| **Spring Actuator** | `/actuator/health` and `/actuator/metrics` must be reachable on the service pod (port 8080). |
| **Environment** | Deploy to `staging` environment (`prod-use1` region) with the same Consul registration as production. |

---

## Pass Criteria
* All test cases execute without errors in the staging environment.
* No duplicate rows are observed in `probe_results` for the same `deviceId` and `jobId` after concurrent attempts.
* Hazelcast map `snmp-walk-locks` contains **zero** entries after each successful or failed execution (lock is always released).
* Application logs contain the expected INFO messages for lock acquisition and release.
* The service health endpoint reports **UP** throughout the test run.

---

**Result Recording**: Document the outcome of each test case in the QA tracker, attach logs, and, if any failure occurs, create a defect linked to PRB‑4821.
