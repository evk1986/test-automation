# Test Plan: Device-Probe Distributed Lock and Metrics Integration (PRB-4821)

## Summary
Validation plan for distributed locking (Hazelcast) and Micrometer metrics integration in Device-Probe under PRB-4821. This test suite verifies lock acquisition, timeout handling, and accurate recording of probe.protocol.failures metrics during simulated NETCONF adapter timeouts on Cisco IOS-XE devices.

## Test cases
1. **Hazelcast Distributed Lock Acquisition & Release**
   - Dispatch JOB-NETCONF-4821 for Cisco IOS-XE target (10.240.12.45) across concurrent worker pods.
   - Verify only one worker acquires lock lock:probe:10.240.12.45 in Hazelcast IMDG.
   - Confirm lock releases cleanly after protocol command execution completes.

2. **NETCONF Protocol Timeout & Micrometer Counter Increment**
   - Inject a 15-second response delay on NETCONF port 830 for target device 10.240.12.46 in staging.
   - Execute probe job via command queue probe.commands for batch BATCH-PRB-20240523-USE1-01.
   - Observe circuit breaker trip on timeout and execution fallback to error handling.
   - Query Prometheus endpoint /actuator/prometheus for metric probe.protocol.failures{protocol="NETCONF",vendor="Cisco-IOS-XE",reason="TIMEOUT"}.
   - Confirm metric counter increments by exactly 1 per failed attempt.

3. **DLQ Routing & Metrics Retention**
   - Exceed retry budget (3 attempts) on persistent NETCONF timeout.
   - Verify job status updates to DLQ in Cassandra table probe_jobs.
   - Verify dead-letter message arrives in SQS queue platform.results.dlq.
   - Verify probe.protocol.failures counter reflects total failed retry attempts (3).

## Staging setup
- **SQS Command Queue**: probe.commands
- **SQS DLQ Queue**: platform.results.dlq
- **Cassandra Keyspace/Table**: netatlas_probe.probe_jobs
- **Actuator Prometheus Endpoint**: http://probe-worker-service.staging.netatlas.internal:8081/actuator/prometheus
- **Hazelcast Cluster**: hazelcast-dev-use1.staging.netatlas.internal:5701

## Pass criteria
- Hazelcast distributed locks prevent duplicate simultaneous probes on the same device IP.
- Prometheus metric probe.protocol.failures accurately tracks NETCONF timeout failures tagged with protocol, vendor, and error reason.
- Probe jobs failing due to NETCONF timeout are correctly routed to platform.results.dlq after budget exhaustion.