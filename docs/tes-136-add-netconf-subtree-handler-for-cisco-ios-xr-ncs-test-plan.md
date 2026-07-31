# Test Plan – TES‑136 – Add NETCONF Subtree Handler for Cisco IOS‑XR NCS

## Summary
This test plan validates the new `ProbeHandlersNetconfSubtreeHandler` and its supporting service. The focus is on correct lock acquisition, metric emission, and graceful handling of failures. All tests are executed against the staging environment.

## Test Cases
1. **Happy Path – Successful Processing**
   - Enqueue a `ProbeJobMessage` with protocol `NETCONF`, deviceFamily `IOS‑XR`, batchId `BATCH-PRB-20240523-USE1-01` and a well‑formed XML payload.
   - Verify that the Hazelcast lock `netconf-lock‑<batchId>-<deviceId>` is acquired and released.
   - Confirm that the service parses the hostname and logs the JSON representation.
   - Ensure the `probe.protocol.failures` counter is **not** incremented.

2. **Lock Contention – Duplicate Session Prevention**
   - Start two parallel consumers processing messages for the same `deviceId` within the same batch.
   - The first consumer should acquire the lock; the second should fail to acquire it within the timeout.
   - Verify that the second invocation increments the failure counter and does **not** call the service.

3. **Parsing Failure – Invalid XML**
   - Submit a message with malformed XML.
   - Expect the service to throw an exception, the handler to catch it, and the failure counter to increment.
   - Confirm that the lock is released even on error.

4. **Protocol/Family Filter – Non‑NETCONF Message**
   - Send a message with protocol `SNMP` or deviceFamily `NX‑OS`.
   - Verify that the handler returns immediately without touching Hazelcast or metrics.

## Staging Setup
- **SQS Queue**: `probe.commands` (standard queue, visibility timeout 30 s).
- **Hazelcast**: Cluster reachable via `hazelcast-staging.internal:5701`.
- **Cassandra Table**: `netatlas.probe_job` (used by other services, not touched by this change).
- **Actuator Endpoint**: `http://staging-probe.internal:8080/actuator/metrics/probe.protocol.failures`.
- **Metrics Tags**: `protocol=NETCONF`, `region=us-east-1`.

## Pass Criteria
- All four test cases execute without errors.
- The failure counter reflects exactly the number of lock‑contention and parsing‑error events.
- No duplicate NETCONF sessions are observed in the logs for the same device within a batch.
- `/actuator/metrics/probe.protocol.failures` returns a non‑negative count with the expected tags.
