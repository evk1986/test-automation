# Runbook – Hazelcast Distributed Lock for Device‑Probe (PRB‑874)

## 1. Overview
The Device‑Probe service now uses a **Hazelcast CP‑Subsystem fenced lock** to coordinate exclusive access to a probe batch. A Micrometer timer metric `probe.lock.duration` records how long each lock is held. This runbook describes deployment, verification, monitoring, and rollback procedures.

## 2. Deployment Steps
1. **Update Helm Chart** – Increment `probe.image.tag` to the version containing `ConfluenceRunbookAdr0093`.
2. **Configure Hazelcast** – Ensure the `hazelcast-probe` CP subsystem is enabled in `values.yaml`:
   ```yaml
   hazelcast:
     cpSubsystem:
       enabled: true
       sessionTTLSeconds: 300
   ```
3. **Apply ConfigMap** – Add lock‑related properties to `probe-config` ConfigMap:
   - `probe.lock.key.prefix = probe-lock-`
   - `probe.lock.metric.name = probe.lock.duration`
4. **Rollout** – Execute `helm upgrade` targeting the `dev`, `staging`, and finally `prod-use1` environments.
5. **Health Check** – After rollout, call:
   `GET /actuator/health` – should report `status: "UP"` and `components.hazelcast.status: "UP"`.

## 3. Verification
| Step | Command | Expected Result |
|------|---------|-----------------|
| 3.1 | `curl -s http://<pod>/api/v1/probe/locks/BATCH-PRB-20240523-USE1-01` | JSON with `locked: false` (no active batch) |
| 3.2 | Trigger a probe batch via SQS (`probe.commands`) | Endpoint now returns `locked: true` and a valid `ownerUuid` |
| 3.3 | `curl http://<pod>/actuator/metrics/probe.lock.duration` | Metric present with a `count` > 0 and a non‑zero `totalTime` |
| 3.4 | Complete the batch (Cassandra write finishes) | Endpoint reverts to `locked: false`; metric `totalTime` reflects lock hold duration |

## 4. Monitoring & Alerting
- **Prometheus Query**: `probe_lock_duration_seconds_sum` – total lock time per batch.
- **Alert**: Fire if `probe_lock_duration_seconds_sum` > 300 seconds for any batch (possible deadlock).
- **Grafana Dashboard**: `Device‑Probe Lock Metrics` (pre‑built in the platform repo).

## 5. Troubleshooting Guide
| Symptom | Likely Cause | Remediation |
|---------|--------------|-------------|
| Endpoint returns HTTP 500 | Hazelcast CP subsystem not reachable | Verify Consul service `hazelcast-probe` resolves; check network ACLs |
| Metric missing | Micrometer binding not loaded | Ensure `micrometer-registry-prometheus` dependency is present and `management.metrics.enable` includes `probe.lock.duration` |
| Lock never releases | Batch crashed without cleanup | Manually force unlock: `hazelcastInstance.getCPSubsystem().getLock("probe-lock-<batchId>").unlock()` via a temporary admin pod |

## 6. Rollback Procedure
1. **Revert Helm Release** – `helm rollback <release> <previous-revision>`.
2. **Remove ConfigMap entries** – Delete `probe.lock.*` keys.
3. **Confirm** – Endpoint should now respond with HTTP 404 (lock endpoint removed) and the metric disappears from Prometheus.
4. **Post‑Rollback Validation** – Run a probe batch; ensure processing completes without lock contention.

---

# Architecture Decision Record – Hazelcast Lock & Metric (ADR‑0093)

**Status**: Accepted

**Context**
- Device‑Probe runs up to 20 000 concurrent device polls every 10 minutes. Without coordination, multiple workers could process the same batch, causing duplicate traffic and inconsistent state.
- Existing Redis‑based lock was unreliable under high churn and lacked CP guarantees.

**Decision**
- Adopt **Hazelcast CP‑Subsystem fenced lock** as the authoritative distributed lock for batch execution.
- Expose lock acquisition duration via a **Micrometer timer** named `probe.lock.duration`.

**Consequences**
- **Positive**: Strong consistency, automatic fail‑over, and built‑in lease expiration prevent deadlocks.
- **Negative**: Introduces a CP dependency; requires CP‑Subsystem configuration and monitoring of quorum health.

**Lock Key Schema**
- Prefix: `probe-lock-`
- Full key: `<prefix><batchId>` (e.g., `probe-lock-BATCH-PRB-20240523-USE1-01`).
- Keys are stored in the CP‑Subsystem map; TTL is governed by the lock lease (default 300 seconds).

**Metric Naming**
- Metric: `probe.lock.duration`
- Type: `Timer`
- Tags: `batchId`, `region` (derived from the batch payload).
- Recorded when the lock is released; value represents the elapsed hold time in seconds.

**References**
- Hazelcast CP‑Subsystem documentation
- Micrometer Timer API guide
- Platform ADR repository – ADR‑0093 linked from this runbook
