# Test Plan – TES‑166 – Add NETCONF Subtree Handler for Cisco IOS‑XR NCS Devices

## Summary
This test plan validates the new `ProbeHandlersNetconfNcsHandler` and its supporting service `NetconfNcsProcessingService`. The focus is on correct lock handling, successful subtree extraction, timeout handling, and Micrometer metric emission.

## Test Cases
1. **Happy Path – Successful NETCONF fetch**
   - Arrange a `ProbeJobMessage` with deviceId `NCS-01`.
   - Mock `NetconfAdapter.fetchSubtree` to return a well‑formed XML payload.
   - Verify that:
     * Hazelcast lock is acquired and released.
     * `DeviceInfo.fromRaw` is invoked.
     * `ProbeJobRepository.save` records status `SUCCESS`.
     * Counter `probe.protocol.failures` remains at **0**.
2. **Timeout Scenario**
   - Mock `NetconfAdapter.fetchSubtree` to throw `NetconfTimeoutException`.
   - Verify that:
     * Lock is released.
     * Job status is persisted as `FAILED` with the timeout message.
     * Counter `probe.protocol.failures` increments to **1**.
3. **Concurrent Execution Guard**
   - Simulate two threads calling `process` for the same deviceId.
   - Configure the lock mock so the second call fails `tryLock()`.
   - Verify that the second invocation logs a warning and does **not** call the adapter.
4. **Metrics Exposure**
   - After executing cases 1 and 2, query `/actuator/metrics/probe.protocol.failures`.
   - Confirm tags `protocol=netconf` and `region=us-east-1` are present and counts match expectations.

## Staging Setup
- **SQS Queue**: `probe.commands`
- **Cassandra Table**: `probe_job` (key: `job_id`)
- **Hazelcast**: default cluster configuration used by the service.
- **Actuator Endpoint**: `http://staging-probe.internal:8080/actuator/metrics/probe.protocol.failures`

## Pass Criteria
- All four test cases execute without errors.
- The failure counter reflects exactly the number of timeout occurrences.
- No duplicate NETCONF walks are observed in the logs when the lock is contested.
- Metrics are visible via the actuator endpoint with correct tags.
