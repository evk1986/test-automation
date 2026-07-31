# Test Plan – TES‑169 – Add Hazelcast Lock and Micrometer Failure Metrics

## Summary
This test plan validates the implementation of a distributed Hazelcast lock that serialises SNMP walk executions per `device-id` and the Micrometer counter `probe.protocol.failures` that records protocol‑specific failures.

## Test Cases
1. **Lock Serialization**
   - **Given** two `ProbeJobMessage` instances with the same `deviceId` and batch `BATCH-PRB-20240523-USE1-01`.
   - **When** the first message acquires the lock and starts the SNMP walk.
   - **Then** the second message should detect that the lock is already held and skip execution (log entry present, no SNMP call).
2. **Failure Counter Increment**
   - **Given** a message that triggers an exception inside `SnmpAdapter.walk`.
   - **When** the service processes the message.
   - **Then** the Micrometer counter `probe.protocol.failures` must be incremented with tags `protocol=SNMP` and `region=<message.region>`.
3. **Successful Execution Does Not Increment Counter**
   - **Given** a normal SNMP walk that completes without exception.
   - **When** the service processes the message.
   - **Then** the counter must remain unchanged.
4. **Lock Release Guarantees**
   - **Given** a successful or failed walk.
   - **When** processing completes.
   - **Then** the Hazelcast lock must be released, allowing subsequent jobs for the same device.
5. **Metrics Exposure**
   - **Given** a running instance in `dev` environment.
   - **When** a failure occurs.
   - **Then** the metric should be visible at `GET /actuator/metrics/probe.protocol.failures` and scraped by Prometheus.

## Staging Setup
- **SQS Queue**: `probe.commands`
- **Cassandra Table** (unchanged for this change): `device_snapshot`
- **Hazelcast Map**: `PROBE_LOCKS` (distributed lock map)
- **Actuator Endpoint**: `http://<pod-ip>:8080/actuator/metrics/probe.protocol.failures`
- **Prometheus**: scrape `/actuator/prometheus` from each probe pod.

## Pass Criteria
- All test cases execute without errors.
- The lock prevents concurrent SNMP walks for the same device.
- The failure counter increments exactly once per exception.
- No regression observed for NETCONF or other protocol handlers.
- Metrics appear in Prometheus with correct tags.
