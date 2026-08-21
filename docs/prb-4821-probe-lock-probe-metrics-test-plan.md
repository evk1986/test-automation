# Test Plan: Device-Probe Lock and Metrics Integration (PRB-4821)

## Summary
This test plan defines the validation criteria for distributed locking (Hazelcast) and Micrometer metric collection within the `Device-Probe` service under ticket PRB-4821. Specifically, this test plan verifies that when a simulated NETCONF timeout occurs on Cisco IOS-XE devices (`ASR-9010`, `ISR-4451`) during batch execution (`BATCH-PRB-20240523-USE1-01`), the `probe.protocol.failures` metric counter in Prometheus is accurately incremented with appropriate tags (`protocol="NETCONF"`, `reason="timeout"`, `device_family="ios-xe"`), and the corresponding Hazelcast distributed lock is properly released without leaving dangling locks.

## Staging Setup
- **SQS Queue**: `probe.commands` (ingest queue for incoming NETCONF probe jobs)
- **DLQ Queue**: `platform.results.dlq` (dead-letter queue for exhausted retries)
- **Cassandra Table**: `netatlas_probe.probe_jobs` (stores state transitions for `ProbeJob` and execution snapshots)
- **Hazelcast Cluster**: 3-node Hazelcast cluster in `staging` with distributed lock map `probe-execution-locks`
- **Actuator & Prometheus Endpoint**: `http://probe-worker.staging.internal:8080/actuator/prometheus`
- **Target Device Types**: Cisco IOS-XE (`ASR-9010`, `ISR-4451`), Cisco IOS-XR (`NCS-5501`)
- **Environment**: `staging` (AWS region `us-east-1`)

## Test Cases

### 1. NETCONF Connection Timeout Metric Increment
- **Procedure**:
  1. Inject an artificial socket latency/timeout (15,000ms) on mock Cisco IOS-XE target (`ASR-9010`, job ID `JOB-NETCONF-4821`).
  2. Dispatch `ProbeJobMessage` via SQS queue `probe.commands` for batch `BATCH-PRB-20240523-USE1-01`.
  3. Query Prometheus endpoint `/actuator/prometheus` or execute PromQL:
     `sum(increase(probe_protocol_failures_total{protocol="NETCONF",reason="timeout"}[5m]))`
- **Expected Outcome**: The metric `probe_protocol_failures_total{protocol="NETCONF",reason="timeout",device_family="ios-xe"}` increases by exactly 1. Cassandra record `ProbeJob` updates status to `FAILED` with `lastErrorMessage` capturing `NetconfSocketTimeoutException`.

### 2. Distributed Lock Release Verification Upon Timeout Failure
- **Procedure**:
  1. Acquire lock key `lock:device:ASR-9010` in Hazelcast map `probe-execution-locks` during probe execution.
  2. Trigger NETCONF command execution failure via device endpoint simulated drop.
  3. Inspect Hazelcast IMap `probe-execution-locks` status via Consul health route and Hazelcast Management Center API `/hazelcast/rest/maps/probe-execution-locks/lock:device:ASR-9010`.
- **Expected Outcome**: The lock `lock:device:ASR-9010` is released cleanly within the `finally` block of the execution service. No lock leak is observed, allowing subsequent retry attempts.

### 3. Metric Tagging Consistency Across Protocol Failure Categories
- **Procedure**:
  1. Execute a batch of 10 NETCONF probe jobs, forcing 3 authentication failures, 4 connection timeouts, and 3 successful responses.
  2. Query Actuator Prometheus endpoint `/actuator/prometheus` for metric `probe_protocol_failures_total`.
- **Expected Outcome**: Prometheus reports `probe_protocol_failures_total{protocol="NETCONF",reason="auth_failure"}` count incremented by 3, `probe_protocol_failures_total{protocol="NETCONF",reason="timeout"}` count incremented by 4, and `probe_protocol_success_total{protocol="NETCONF"}` count incremented by 3.

### 4. SQS Retry Budget and DLQ Routing on Persistent Timeout
- **Procedure**:
  1. Set attempt count to maximum retry budget (3) for job `JOB-NETCONF-4821`.
  2. Trigger final NETCONF timeout execution on `probe.commands`.
- **Expected Outcome**: Message is routed to `platform.results.dlq`. Hazelcast lock is unlocked, and Cassandra status is marked as `DLQ`.

## Pass Criteria
1. The metric `probe.protocol.failures` is exposed via Micrometer `/actuator/prometheus` and increments synchronously on NETCONF session timeouts.
2. Prometheus tags `protocol`, `reason`, and `device_family` are correctly populated without null or missing values.
3. Hazelcast distributed lock `probe-execution-locks` is unconditionally released after every failure event.
4. Cassandra table `probe_jobs` reflects `status="FAILED"` and accurate `attemptCount`.
5. No unhandled exceptions or thread leaks occur in `Device-Probe` application logs.
