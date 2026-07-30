# Summary
Implement a Hazelcast distributed lock for SNMP walk workers in the **Device‑Probe** service and expose a Micrometer counter `snmp.lock.contention` to monitor lock contention. While the lock is held, the SQS message visibility timeout is extended to 300 seconds.

# Test Cases
1. **Lock acquisition succeeds** – the worker sets the SQS visibility timeout, the contention counter is **not** incremented, and the lock is released.
2. **Lock acquisition fails** – the contention counter is incremented, no visibility‑timeout request is sent, and the lock is never unlocked.
3. **Metric exposure** – after exercising the code, the metric appears at `/actuator/metrics/snmp.lock.contention` with a non‑negative value.

# Staging Setup
- **Queue**: `probe.commands`
- **Hazelcast lock key pattern**: `snmpLock-{deviceId}`
- **Visibility timeout**: 300 seconds (5 minutes)
- **Actuator endpoint**: `http://dev-host:8080/actuator/metrics/snmp.lock.contention`

# Pass Criteria
- All unit tests in `ProbeWorkerSnmpHazelcastLockTest` pass.
- The Micrometer counter increments on lock contention.
- The metric is visible via the Actuator endpoint.
- No SQS visibility‑timeout call occurs when the lock is contended.
