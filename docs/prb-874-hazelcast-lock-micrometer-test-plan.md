# PRB-874 – Hazelcast Lock for SNMP Walks & Protocol Failure Metrics

## Summary
Add a distributed Hazelcast lock around each SNMP walk to prevent concurrent walks against the same device. Increment a Micrometer counter (`probe.protocol.failure`) whenever the lock cannot be acquired or an exception occurs during the walk.

## Test Cases
1. **Lock Contention** – Simulate a scenario where `FencedLock.tryLock()` returns `false`. Verify that the service throws `IllegalStateException` and the failure counter is incremented exactly once.
2. **Successful Walk** – Simulate successful lock acquisition. Verify that the walk completes without incrementing the failure counter and that the lock is released.
3. **Exception During Walk** – Force an unchecked exception after lock acquisition. Verify that the counter is incremented and the lock is released in the `finally` block.

## Staging Setup
- **SQS Queue**: `probe.commands` (already provisioned).
- **Cassandra Table**: `device_snapshot` – not touched by this change.
- **Hazelcast**: Ensure the CP subsystem is enabled; the lock name pattern is `snmp-walk-{deviceId}`.
- **Micrometer**: Counter `probe.protocol.failure` is exported via Prometheus (`/actuator/prometheus`).
- **Actuator Endpoints**: Verify `/actuator/metrics/probe.protocol.failure` reflects increments after test execution.

## Pass Criteria
- All unit tests in `HazelcastLockImplementationMicrometerCounterTest` pass (`mvn verify`).
- The Prometheus metric `probe_protocol_failure_total{protocol="SNMP"}` increments on lock contention and walk failures.
- No dead‑locks observed; the lock is always released in the `finally` block.
