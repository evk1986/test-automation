# QA Test Plan: TES-178 - Distributed Hazelcast Lock and Micrometer Protocol Failure Metrics

## Summary
Validates the execution of SNMP walk tasks guarded by Hazelcast distributed locking and the emission of `probe.protocol.failures` metrics to Prometheus when protocol operations or lock acquisitions fail.

## Test Cases

1. **Concurrent Device Probe Lock Acquisition**
   - Trigger two concurrent SQS probe commands with identical `batchId` (`BATCH-PRB-20240523-USE1-01`) and `deviceId` (`DEV-ASR-9001`).
   - Verify that only one worker acquires the Hazelcast fenced lock while the second worker waits or times out safely without dual execution.

2. **Protocol Failure Counter Emission**
   - Simulate an SNMP timeout during probe execution for device `DEV-ASR-9001` in region `prod-use1`.
   - Verify that Micrometer increments `probe.protocol.failures` with tags `protocol=SNMP` and `region=prod-use1`.

3. **Lock Release On Exception**
   - Inject an unexpected exception into the execution closure while the lock is acquired.
   - Verify that the lock is released in the `finally` block and subsequent tasks for the device can execute.

## Staging Setup
- **Queue Name:** `probe.commands`
- **Cassandra Table:** `probe_jobs`
- **Actuator Endpoint:** `http://localhost:8081/actuator/metrics/probe.protocol.failures`

## Pass Criteria
- Lock prevents concurrent SNMP walks for the same device ID in the same batch.
- Prometheus counter `probe.protocol.failures` emits expected tags `protocol` and `region`.
- All unit and integration test suites pass successfully.
