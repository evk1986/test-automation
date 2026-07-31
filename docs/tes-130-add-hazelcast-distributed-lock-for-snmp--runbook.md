# Runbook – Deploy Hazelcast Distributed Lock for SNMP Walk (TES‑130)

## Purpose
Introduce a Hazelcast‑based distributed lock to guarantee that only one SNMP walk runs per device within a batch. This prevents race conditions and reduces load on network devices.

## Preconditions
- Hazelcast cluster is healthy and reachable from all **Device‑Probe** pods.
- Micrometer metrics are being scraped by Prometheus.
- The new Docker image `netatlas/probe:2.7.7‑hazelcast‑lock‑v1` has been pushed to the registry.
- Existing batch `BATCH-PRB-20240523-USE1-01` is scheduled in the staging environment.

## Deployment Steps
1. **Update Helm values**
   ```yaml
   probe:
     image: netatlas/probe:2.7.7-hazelcast-lock-v1
     hazelcast:
       enabled: true
   ```
2. **Apply the manifest**
   ```bash
   helm upgrade probe ./helm/probe -f values-prod-use1.yaml
   ```
3. **Verify pod rollout**
   ```bash
   kubectl rollout status deployment/probe -n netatlas
   ```
4. **Check lock metrics**
   ```bash
   curl http://probe-prod-use1.internal:8080/actuator/metrics/snmp.lock.acquired
   curl http://probe-prod-use1.internal:8080/actuator/metrics/snmp.lock.released
   ```
5. **Run a smoke test**
   - Publish a single `ProbeJobMessage` to `probe.commands` for a known device.
   - Confirm logs contain `Lock acquired` and `Lock released` messages.
   - Verify the corresponding counters increment.

## Rollback Procedure
If any of the following conditions are observed, rollback to the previous image:
- Lock acquisition failures exceed 5 % of total SNMP jobs.
- Unexpected increase in device CPU usage.
- Missing metrics in Prometheus.

Rollback command:
```bash
helm rollback probe <previous-release-number> -n netatlas
```

## Monitoring & Alerts
- **Alert**: `SnmpLockAcquisitionFailure` – fires when `snmp.lock.acquired_total` does not increase for 5 consecutive minutes while `probe.commands` queue depth is > 0.
- **Dashboard**: `netatlas-probe-locks` (Grafana) shows real‑time lock acquisition/release rates per region.

## Post‑Deployment Validation
- Run the full batch `BATCH-PRB-20240523-USE1-01` in staging.
- Confirm no duplicate SNMP walk logs for any device.
- Verify downstream services (Schema‑Normalizer, Data‑Enricher) receive the same number of messages as before.
