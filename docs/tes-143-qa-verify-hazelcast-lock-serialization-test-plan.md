# Test Plan – Verify Hazelcast Lock Serialization & Protocol Failure Metrics (TES-143)

## Summary
This test validates that concurrent SNMP walk jobs pulled from the `probe.commands` queue are serialized per‑device using a Hazelcast distributed lock. It also confirms that the Micrometer counter `probe.protocol.failures` is incremented exactly for each injected failure.

## Test Cases
1. **Concurrent Walk Simulation**
   - Publish 20 `ProbeJobMessage` records for 5 distinct device IDs (4 messages per device) to `probe.commands`.
   - Enable parallel SQS listeners (default Spring Cloud AWS concurrency).
   - Verify via log inspection that for each device only one `Acquired lock` entry appears before a corresponding `Released lock` entry.
2. **Duplicate Walk Detection**
   - After the run, assert that no log line contains "Another SNMP walk is already in progress" for the same device while a walk is active.
3. **Failure Counter Accuracy**
   - Set the `injectFailure` flag on 3 of the 20 messages.
   - After processing, scrape `/actuator/metrics/probe.protocol.failures`.
   - Expected counter value = 3.
4. **Metric Reset Verification**
   - Reset the SimpleMeterRegistry between test runs and repeat case 3 to ensure the counter starts at 0.

## Staging Setup
- **Queue**: `probe.commands` (standard SQS queue, no DLQ for this test).
- **Cassandra Table**: not used in this integration test.
- **Actuator Endpoint**: `http://localhost:8080/actuator/metrics/probe.protocol.failures`.
- **Hazelcast**: Embedded instance configured via `hazelcastInstance` bean in the test profile.
- **Spring Profile**: `test` – disables external SNS publishing.

## Pass Criteria
- No duplicate SNMP walk logs for any device ID.
- The failure counter value matches the number of messages where `injectFailure=true`.
- All locks are released after processing completes.

---
*Prepared for ticket TES-143 (PRB-874).*