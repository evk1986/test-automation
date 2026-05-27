# Test Plan: Implement Hazelcast Distributed Lock in Device-Probe
## Summary
This test plan covers the implementation of a Hazelcast distributed lock in the Device-Probe service to prevent concurrent SNMP walks for the same device-id within the same batch.
## Test Cases
1. **Successful lock acquisition**: Verify that the Hazelcast distributed lock is acquired successfully for a given device-id and batch-id.
2. **Concurrent SNMP walks prevention**: Verify that concurrent SNMP walks for the same device-id within the same batch are prevented by the Hazelcast distributed lock.
## Staging Setup
* Queue names: probe.commands
* Cassandra table: probe_jobs
* Actuator endpoint: /actuator/health
## Pass Criteria
* Successful implementation of Hazelcast distributed lock
* No concurrent SNMP walks for the same device-id within the same batch