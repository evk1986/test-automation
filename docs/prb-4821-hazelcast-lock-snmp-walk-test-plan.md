# Summary
Add a Hazelcast distributed lock to the SNMP walk operation to prevent concurrent walks on the same device. The lock is defined in `HazelcastLockBeanUpdateSnmp` and used by `SnmpWalkService`.

# Test Cases
1. **Lock Acquisition Success** – Verify that when the lock is available, `SnmpWalkService.performWalk` acquires it, performs the walk, sets job status to `SUCCESS`, and releases the lock.
2. **Lock Acquisition Failure** – Verify that when the lock cannot be acquired, the job status is set to `DLQ` and the lock is not released.
3. **Interrupted Exception Handling** – Simulate an `InterruptedException` during the walk and ensure the job status becomes `FAILED` and the thread interrupt flag is preserved.

# Staging Setup
- **Queue**: `probe.commands`
- **Cassandra Table**: `probe_job` (fields `device_id`, `status`, `last_error_message`)
- **Actuator Endpoint**: `/actuator/health` and `/actuator/metrics/hazelcast.lock.acquire`
- **Hazelcast**: Ensure a Hazelcast cluster is running and the lock bean `snmpWalkLock` is available.

# Pass Criteria
- All unit tests in `SnmpWalkServiceTest` pass.
- Integration test on batch `BATCH-PRB-20240523-USE1-01` shows no overlapping SNMP walks for the same device.
- Metrics `hazelcast.lock.acquire` increment only once per device per batch.
