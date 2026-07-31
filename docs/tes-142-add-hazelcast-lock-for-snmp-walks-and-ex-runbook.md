# Runbook – TES‑142 – Hazelcast Lock & Protocol Failure Metrics

## Purpose
This runbook documents the operational steps required to verify, monitor, and troubleshoot the Hazelcast distributed lock that protects SNMP walk execution and the Micrometer failure counter introduced for the **Device‑Probe** service.

## Prerequisites
- Access to the `dev` or `staging` Kubernetes namespace where `device-probe` pods run.
- `kubectl` configured with appropriate RBAC.
- Access to the Prometheus UI (or Grafana dashboard) that scrapes `/actuator/prometheus`.
- Hazelcast Management Center (optional) for lock inspection.

## Verification Steps
1. **Deploy the Updated Image**
   ```bash
   kubectl rollout restart deployment/device-probe -n dev
   ```
   Wait for the rollout to complete.

2. **Confirm Lock Creation**
   - Open Hazelcast Management Center (`http://hazelcast-mc.dev.svc.cluster.local:8080`).
   - Navigate to **CP Subsystem → Locks**.
   - Verify that lock names follow the pattern `snmp-walk-lock-<batchId>-<deviceId>`.

3. **Trigger a SNMP Walk Batch**
   - Publish a test message to the `probe.commands` queue (AWS CLI example):
   ```bash
   aws sqs send-message --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands \
       --message-body '{"deviceId":"device-001","batchId":"BATCH-PRB-20240523-USE1-01","protocol":"SNMP","region":"us-east-1"}'
   ```
   - Observe the pod logs for lock acquisition and release messages.

4. **Check Failure Metric**
   - Access the actuator endpoint:
   ```bash
   curl http://device-probe.dev.svc.cluster.local:8080/actuator/metrics/probe.protocol.failures
   ```
   - Verify the JSON payload contains a `measurements[0].value` that matches the expected failure count and includes tags `protocol=SNMP` and `region=us-east-1`.

5. **Grafana Dashboard**
   - Open the *Device Probe Metrics* dashboard.
   - Locate the panel **SNMP Failure Rate** which queries:
   ```promql
   probe_protocol_failures{protocol="SNMP"}
   ```
   - Ensure the graph updates after each simulated failure.

## Troubleshooting
| Symptom | Possible Cause | Action |
|---------|----------------|--------|
| No lock appears in Management Center | Hazelcast client mis‑configured or CP subsystem not started | Verify `hazelcastInstance` bean configuration in `ProbeConfig`. Check pod logs for CP subsystem errors. |
| Failure counter stays at 0 despite errors | Counter tags mismatched or registry not shared | Ensure `recordFailure` uses the same `MeterRegistry` instance as the actuator. Restart the pod after code changes. |
| Duplicate SNMP walks for the same device | Lock timeout too short, causing premature release | Increase `LOCK_TIMEOUT` in `HazelcastLockImplementationMicrometerCounter` or investigate long‑running SNMP walks. |
| High latency on `/actuator/metrics` endpoint | Large number of distinct tag combinations | Review tag cardinality; consider aggregating by region only if appropriate. |

## Rollback Procedure
1. Deploy the previous Docker image tag (e.g., `device-probe:2.7.6‑prev`).
2. Verify that the `probe.commands` queue is still being processed without lock usage.
3. Confirm that the `probe.protocol.failures` metric is no longer emitted.

---
*Runbook authored for ticket PRB‑874 (TES‑142) by the junior backend contractor.*