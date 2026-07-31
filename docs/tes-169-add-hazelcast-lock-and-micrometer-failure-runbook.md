# Runbook – TES‑169 – Hazelcast Lock & Micrometer Failure Metrics

## Purpose
Provide operators with the steps to verify, monitor, and, if necessary, roll back the Hazelcast lock and Micrometer counter introduced for SNMP walks in the **Device‑Probe** service.

## Prerequisites
- Access to the `dev` or `staging` Kubernetes namespace where `device-probe` pods run.
- `kubectl` configured with appropriate context.
- `curl` or `httpie` for hitting Actuator endpoints.
- Prometheus UI access (or `promtool query`).

## Verification Steps
1. **Confirm Hazelcast Map Exists**
   ```bash
   kubectl exec -it $(kubectl get pod -l app=device-probe -o jsonpath='{.items[0].metadata.name}') \
       -- curl -s http://localhost:5701/hazelcast/rest/maps/PROBE_LOCKS
   ```
   The response should be an empty JSON object if no locks are held.
2. **Trigger Two SNMP Walks for the Same Device**
   - Publish two `ProbeJobMessage` payloads with identical `deviceId` to the `probe.commands` queue (use AWS CLI or localstack).
   - Observe pod logs:
     ```bash
     kubectl logs -l app=device-probe -c app | grep "SNMP walk already in progress"
     ```
   - Only one `SnmpAdapter.walk` call should appear.
3. **Check Failure Counter**
   - Force an exception (e.g., set an invalid community string) and re‑run a walk.
   - Query the metric:
     ```bash
     curl -s http://<pod-ip>:8080/actuator/metrics/probe.protocol.failures | jq '.'
     ```
   - Verify `measurements[0].value` increased and tags include `protocol=SNMP` and the correct `region`.
4. **Prometheus Scrape Verification**
   - Open Prometheus UI → *Graph* and run:
     ```promql
     probe_protocol_failures{protocol="SNMP",region="us-east-1"}
     ```
   - The series should reflect the increment observed in step 3.

## Rollback Procedure
If the new lock causes unexpected throttling:
1. **Scale Down the Updated Deployment**
   ```bash
   kubectl rollout pause deployment/device-probe
   ```
2. **Re‑deploy the previous image tag** (e.g., `device-probe:1.4.2`)
   ```bash
   kubectl set image deployment/device-probe device-probe=device-probe:1.4.2
   ```
3. **Resume Rollout**
   ```bash
   kubectl rollout resume deployment/device-probe
   ```
4. **Validate** that the old behaviour (no lock, no new metric) is restored.

## Monitoring & Alerts
- **Alert**: `ProbeProtocolFailuresHigh` – fire when `probe_protocol_failures` rate > 5/min for any region.
- **Dashboard**: Add a Grafana panel showing the counter over time, broken out by `region`.

## Contact
- Service Owner: **Device‑Probe Team** (Slack: `#netatlas-probe`)
- On‑call: `oncall-netatlas@company.com`
