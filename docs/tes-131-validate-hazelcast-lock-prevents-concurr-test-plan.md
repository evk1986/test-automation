# Test Plan – TES-131 – Validate Hazelcast Lock Prevents Concurrent SNMP Walks

## Summary
This test plan validates that the newly introduced Hazelcast distributed lock serialises SNMP walk executions per device. The test executes a staged batch **BATCH-PRB-20240523-USE1-01** against a simulated environment and inspects logs, lock metrics, and Cassandra tables for duplicate records.

## Test Cases
1. **Lock Acquisition Logging**
   - **Steps**: Deploy the updated `device-probe` service to the `staging` environment. Trigger the batch via the orchestrator. Observe the application logs for the message `Attempting to acquire Hazelcast lock for device <device-id>` followed by `Lock acquired for device <device-id>`.
   - **Expected Result**: Each device in the batch logs both messages before any SNMP walk begins.

2. **No Duplicate SNMP Walk Records**
   - **Steps**: After batch completion, query the Cassandra table `probe.results` for the `job_id` associated with each device. Count the rows per `device_id`.
   - **Expected Result**: Exactly one row per device; duplicate rows indicate lock failure.

3. **Lock Release Verification**
   - **Steps**: Query Hazelcast metrics (`/hazelcast/metrics`) or inspect the management console for lock entries named `snmp-walk-<device-id>` after the batch finishes.
   - **Expected Result**: No lock entries remain; all locks have been released.

4. **Concurrent Invocation Stress Test**
   - **Steps**: Simulate 5 concurrent SQS messages targeting the same device ID. Verify that only one SNMP walk is performed and the others wait for the lock.
   - **Expected Result**: Log shows sequential lock acquisition; Cassandra contains a single walk record.

## Staging Setup
- **AWS SQS Queue**: `probe.commands`
- **Cassandra Table**: `probe.results` (primary key: `device_id, job_id`)
- **Hazelcast Cluster**: Accessible via service discovery (`hazelcast-client` bean)
- **Actuator Endpoint**: `http://staging-device-probe.internal:8080/actuator/health`

## Pass Criteria
All test cases pass without any duplicate SNMP walk entries, and lock metrics show zero lingering locks after processing.
