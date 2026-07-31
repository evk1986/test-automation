# Summary
Validate that Micrometer counters for protocol failures are emitted by the Device‑Probe service and are visible to Prometheus.

# Test cases
1. Deploy the dev stack with Prometheus and Grafana.
2. Inject a mock NETCONF device that returns a failure response.
3. Verify that the `/actuator/metrics` endpoint contains `device_probe_protocol_failure_total{protocol="NETCONF"}` with an incremented value.
4. Repeat steps 2‑3 for SSH, SNMP, EAPI, and GRPC protocols.
5. Simulate 11 failures per minute for NETCONF and confirm the alert rule `DeviceProbeProtocolFailureHigh` fires.
6. Run a normal operation scenario with no failures and ensure no alerts are raised.

# Staging setup
- Queue: `probe.commands`
- Cassandra table: `protocol_failure_metrics`
- Actuator endpoint: `http://dev-device-probe:8080/actuator/metrics`
- Prometheus scrape target: `dev-device-probe:8080/actuator/prometheus`
- Grafana dashboard: `Device Probe Protocol Failures`

# Pass criteria
- All five protocol counters increment by at least one after each simulated failure.
- Grafana displays real‑time counter values.
- Alert triggers when failures exceed 10 per minute.
- No alerts during normal operation.
