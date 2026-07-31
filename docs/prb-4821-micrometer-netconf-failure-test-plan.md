# PRB‑4821 – Micrometer NETCONF Failure Counter Test Plan

## Summary
This test plan validates that the new **NETCONF subtree handler** correctly increments the Micrometer counter `probe.protocol.failures` with the tags `protocol=NETCONF` and `region=us-east-1` whenever a NETCONF operation fails. The metric must be observable via the Spring Boot Actuator endpoint `/actuator/metrics/probe.protocol.failures`.

## Test Cases
1. **Successful NETCONF Subtree Call**
   - **Setup**: Mock `NetconfAdapter` to return a valid XML payload.
   - **Action**: Invoke `ProbeHandlersNetconfSubtreeService.processSubtree` with a `ProbeJobMessage`.
   - **Expected Result**: No increment on `probe.protocol.failures`; a `DeviceSnapshot` is persisted.
2. **Failed NETCONF Subtree Call**
   - **Setup**: Mock `NetconfAdapter` to throw an exception.
   - **Action**: Invoke `processSubtree`.
   - **Expected Result**: Counter `probe.protocol.failures` is incremented by **1**; no snapshot is persisted.
3. **Actuator Metric Visibility**
   - **Setup**: Deploy the service to the `staging` environment.
   - **Action**: Trigger a failure (as in test case 2) and query `/actuator/metrics/probe.protocol.failures`.
   - **Expected Result**: JSON response contains a measurement with `value` >= 1 and tags `protocol=NETCONF`, `region=us-east-1`.

## Staging Setup
- **Queue**: `probe.commands` (SQS) – ensure a NETCONF job is enqueued.
- **Cassandra Table**: `device_snapshot` – used by `DeviceSnapshotRepository` (no write expected on failure).
- **Actuator Endpoint**: `http://staging-netatlas.internal:8080/actuator/metrics/probe.protocol.failures`
- **Environment Tags**: Region `us-east-1` (hard‑coded for this ticket).

## Pass Criteria
- All three test cases execute without errors.
- The failure counter reflects the exact number of simulated failures.
- The Actuator metric endpoint returns the expected tags and a non‑negative numeric value.
- No unexpected exceptions are logged to the console during the test runs.
