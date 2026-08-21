# Test Plan: Distributed Lock and Metrics Integration (PRB-4821)

## Summary
Verify that Hazelcast distributed lock mechanisms effectively prevent duplicate device processing during concurrent SNMP walks on the `probe.commands` SQS queue, and confirm that `probe.protocol.failures` metric counter correctly increments upon induced errors.

## Test Cases
1. **Concurrent Message Ingestion & Lock Guarding**
   - Dispatch 50 concurrent SNMP probe command messages for the same device ID (`dev-cisco-asr-01`).
   - Inspect Hazelcast distributed locks and application logs.
   - Confirm only one worker acquires lock while 49 messages log lock contention warnings and exit without triggering duplicate SNMP sweeps.

2. **Protocol Error Failure Metric Counter**
   - Inject connection timeout and SNMP parse errors for target CPEs.
   - Verify `probe.protocol.failures` metric counter increments exactly once per induced failure on Micrometer Actuator endpoint.

3. **Operational REST Endpoint Query**
   - Query `/api/v1/probe/locks-metrics/dev-cisco-asr-01/lock-status` during an active walk.
   - Assert HTTP 200 response returning `locked: true` and status `IN_PROGRESS`.

## Staging Setup
- **SQS Queue Name:** `probe.commands`
- **Cassandra Table:** `probe_jobs`
- **Actuator Endpoint:** `http://localhost:8081/actuator/prometheus`
- **Batch Reference:** `BATCH-PRB-20240523-USE1-01`

## Pass Criteria
- Zero duplicate execution records in Cassandra `probe_jobs` for the same device during concurrent runs.
- Prometheus metric `probe_protocol_failures_total` reflects exact count of simulated protocol exceptions.
