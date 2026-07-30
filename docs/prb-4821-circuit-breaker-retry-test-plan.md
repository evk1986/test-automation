# Summary
Add circuit breaker and retry logic to the NETCONF NCS handler. Instrument a Micrometer timer to capture latency.

# Test Cases
1. Verify that a successful NETCONF operation records latency and returns result without retry.
2. Verify that a transient failure triggers a retry and eventually returns result, and latency includes both attempts.
3. Verify that after three failed attempts the service throws a RuntimeException and records failure.
4. Verify that the timer metric `netconf.ncs.latency` is present in Prometheus endpoint.

# Staging Setup
- Queue: probe.commands
- Cassandra table: device_snapshot
- Actuator endpoint: `/actuator/metrics/netconf.ncs.latency`
- Deploy the service with `SPRING_PROFILES_ACTIVE=staging`.

# Pass Criteria
All test cases pass, the metric appears with count ≥ 1, and no unhandled exceptions are logged during normal operation.
