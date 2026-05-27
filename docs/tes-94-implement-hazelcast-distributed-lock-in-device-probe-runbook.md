# Runbook: Implement Hazelcast Distributed Lock in Device-Probe
## Introduction
This runbook covers the implementation of a Hazelcast distributed lock in the Device-Probe service to prevent concurrent SNMP walks for the same device-id within the same batch.
## Prerequisites
* Hazelcast instance setup
* Device-Probe service setup
## Implementation Steps
1. **Configure Hazelcast instance**: Configure the Hazelcast instance to use the distributed lock feature.
2. **Implement Hazelcast distributed lock**: Implement the Hazelcast distributed lock in the Device-Probe service to prevent concurrent SNMP walks for the same device-id within the same batch.
## Verification Steps
1. **Verify lock acquisition**: Verify that the Hazelcast distributed lock is acquired successfully for a given device-id and batch-id.
2. **Verify concurrent SNMP walks prevention**: Verify that concurrent SNMP walks for the same device-id within the same batch are prevented by the Hazelcast distributed lock.