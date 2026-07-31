# Test Plan – Hazelcast Lock Deployment (TES‑144)

## Summary
This document outlines the verification steps for the new Hazelcast distributed lock used by the **Device‑Probe** service and the accompanying Micrometer metric `probe.lock.duration`. The test plan validates deployment, runtime behavior, and rollback procedures.

## Test Cases
1. **Lock Acquisition** – Trigger a probe batch (`BATCH-PRB-20240523-USE1-01`) and verify that the lock is acquired via the `/api/v1/probe/locks/{batchId}` endpoint.
2. **Metric Emission** – After a successful lock acquisition, confirm that `probe.lock.duration` appears in Prometheus with a non‑zero value.
3. **Concurrent Contention** – Start two identical batch jobs; ensure the second job receives a `LOCKED` response and the metric records a contention count.
4. **Graceful Release** – Upon batch completion, confirm the lock is released and the endpoint reports `locked: false`.
5. **Rollback Scenario** – Deploy the previous version (without the lock), run a batch, and verify that the lock endpoint returns HTTP 404 and the metric is absent.

## Staging Setup
- **Queues**: `probe.commands` (SQS), `normalize.ingest` (SQS)
- **Cassandra Table**: `device_snapshot` (used by downstream services – unchanged)
- **Hazelcast Cluster**: Accessible via Consul service `hazelcast-probe` in the `staging` namespace.
- **Actuator Endpoint**: `http://staging-probe.internal.netatlas.com/actuator/metrics/probe.lock.duration`
- **Prometheus Scrape**: Verify metric appears under the `probe_lock_duration_seconds` series.

## Pass Criteria
- All test cases execute without errors.
- The lock endpoint returns accurate state (`locked`, `ownerUuid`, `lockAcquiredAt`).
- `probe.lock.duration` is recorded for every successful lock acquisition and cleared on release.
- Rollback restores previous behavior with no residual lock artifacts.
