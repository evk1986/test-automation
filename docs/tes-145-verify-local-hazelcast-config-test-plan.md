# Test Plan – TES‑145 – Verify Local Hazelcast Config & Metric Visibility

## Summary
This test plan validates that the local Docker‑Compose environment for **Device‑Probe** starts a Hazelcast client correctly, acquires distributed locks, and exposes the newly added Micrometer counter `probe.protocol.failures` via the Actuator metrics endpoint.

## Test Cases
1. **Start Docker Compose**
   - Run `docker-compose -f docker-compose.probe.yml up -d`.
   - Verify that the `hazelcast` container is healthy and that the `probe` container logs `Hazelcast client started`.
2. **Trigger a SNMP walk**
   - Execute `curl -X POST http://localhost:8080/api/v1/probe/jobs/snmp-walk -d '{"deviceId":"device-123","region":"us-east-1"}'`.
   - Observe console logs for `Acquired lock` and `Released lock` messages.
3. **Check metric endpoint**
   - Call `curl http://localhost:8080/actuator/metrics/probe.protocol.failures`.
   - Confirm the JSON payload contains `measurements[0].value` and a `tags` object with at least the `protocol` tag.
4. **Validate tag values**
   - Ensure the `protocol` tag equals `SNMP` after the walk.
5. **Negative scenario – Hazelcast down**
   - Stop the Hazelcast container (`docker stop hazelcast`).
   - Re‑run the SNMP walk and verify that the probe logs an error `Hazelcast connection failed` and that the metric counter does not increase.

## Staging Setup
- **Queue names**: `probe.commands`, `normalize.ingest`
- **Cassandra keyspace/table**: `netatlas.probe_lock_log` (placeholder, not populated)
- **Actuator endpoint**: `http://localhost:8080/actuator/metrics/probe.protocol.failures`
- **Docker images**: `netatlas/device-probe:local`, `hazelcast/hazelcast:5.3`

## Pass Criteria
- Docker Compose starts without Hazelcast connection errors.
- Lock acquisition and release are logged during the SNMP walk.
- The metric endpoint returns a JSON object with a non‑negative `value` and the expected `protocol` tag.
- When Hazelcast is stopped, the service logs a clear connection error and does not increment the counter.
