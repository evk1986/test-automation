# Test Plan – TES‑137 – Validate NETCONF handler and lock integration

## Summary
This test plan validates the end‑to‑end execution of batch **BATCH-PRB-20240523-USE1-01** on the **staging** environment. It focuses on the NETCONF handler for Cisco IOS‑XR NCS devices, ensuring that:
- NETCONF responses are processed correctly.
- Hazelcast distributed lock is acquired and released without dead‑locks.
- Micrometer counters (`probe.netconf.success`, `probe.netconf.failure`, `probe.netconf.lock.acquired`, `probe.netconf.lock.released`) reflect the expected values.
- No duplicate NETCONF sessions appear in the logs.

## Test Cases
1. **Happy Path Execution**
   - Trigger the batch via the orchestrator.
   - Verify that every `ProbeJobMessage` with protocol `NETCONF` is consumed by `ProbeTestsNetconfHandler`.
   - Assert that the success counter increments by the number of NETCONF jobs.
   - Confirm lock acquisition and release events for each job.
2. **DLQ Replay**
   - Populate `platform.results.dlq` with a set of failed NETCONF messages.
   - Replay the DLQ and ensure the handler processes them, updating the failure counter.
3. **Lock Contention Simulation**
   - Manually acquire a lock for a known job ID using Hazelcast CLI before the batch runs.
   - Run the batch and verify that the handler waits, acquires the lock after release, and does not dead‑lock.
4. **Duplicate Session Guard**
   - Scan the application logs for the pattern `"Duplicate NETCONF session"`.
   - Ensure the pattern never appears.
5. **Metrics Validation**
   - Query Prometheus endpoint `/actuator/prometheus` after batch completion.
   - Validate that `probe.netconf.lock.acquired` equals `probe.netconf.lock.released` and matches the total NETCONF job count.

## Staging Setup
- **AWS SQS Queues**
  - `probe.commands` – source of `ProbeJobMessage`.
  - `platform.results.dlq` – dead‑letter queue for replay.
- **Cassandra Tables**
  - `probe_job` – stores job status.
  - `device_snapshot` – raw NETCONF payloads.
- **Actuator Endpoints**
  - Health: `http://staging-probe.internal.netatlas.com/actuator/health`
  - Metrics: `http://staging-probe.internal.netatlas.com/actuator/prometheus`
- **Hazelcast Cluster** – reachable via `hazelcast.staging.internal.netatlas.com:5701`.
- **Credentials** – Access via Vault role `staging/netatlas/probe`.

## Pass Criteria
- All test cases complete without errors.
- Success counter equals the number of NETCONF jobs in the batch.
- Failure counter is **0** unless DLQ replay is exercised; then it must equal the number of replayed messages.
- Lock acquisition and release counters are equal and match the total NETCONF job count.
- No duplicate NETCONF session warnings in logs.
- Prometheus metrics reflect the above counts.
