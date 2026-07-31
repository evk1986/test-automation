# Runbook – Validate Micrometer Protocol Failure Metrics

## Prerequisites
- Access to the `dev` Kubernetes namespace.
- Prometheus and Grafana deployed in the monitoring stack.
- `kubectl` configured with appropriate context.
- Mock device scripts located in `scripts/mock-devices/`.

## Steps
1. **Deploy Device‑Probe**
   ```bash
   helm upgrade --install device-probe ./helm/device-probe \
     --set environment=dev \
     --set metrics.enabled=true
   ```
2. **Start Prometheus scrape** – ensure the target `dev-device-probe:8080/actuator/prometheus` appears in the Prometheus UI.
3. **Inject failures** – run each mock script to generate a failure for the target protocol, e.g.:
   ```bash
   ./scripts/mock-devices/netconf-failure.sh
   ```
4. **Query metrics** – curl the actuator endpoint and look for the counter:
   ```bash
   curl -s http://dev-device-probe:8080/actuator/metrics/device_probe_protocol_failure_total
   ```
5. **Validate Grafana** – open the *Device Probe Protocol Failures* dashboard and confirm the counters update.
6. **Check alert** – after generating >10 failures within a minute, verify the alert `DeviceProbeProtocolFailureHigh` appears in the Alertmanager UI.
7. **Cleanup** – delete mock devices and optionally roll back the Helm release:
   ```bash
   helm uninstall device-probe
   ```

## Verification
- Counter value for each protocol increased by the number of injected failures.
- Alert fires exactly once per threshold breach.
- No residual alerts after cleanup.

## Rollback
If counters do not appear, revert the Helm release and inspect the `device-probe` logs for Micrometer configuration errors.
