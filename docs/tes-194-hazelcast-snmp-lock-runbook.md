# Runbook: Hazelcast Lock & Idempotency Management for Device-Probe

## Overview
This operational runbook covers troubleshooting procedures for Hazelcast distributed locking and SQS message idempotency in the `Device-Probe` service (PRB-4821).

## Key Metrics & Alerts
- Metric: `probe.snmp.idempotent` (Counter)
  - High rates indicate upstream duplicate dispatching from Fleet-Orchestrator.
- Log Search: `Could not acquire SNMP lock for deviceId=`
  - Indicates lock contention across probe workers.

## Operational Troubleshooting

### Stale Lock Resolution
If an SNMP walk hangs or crashes without releasing `snmp-lock-{deviceId}`:
1. Inspect Hazelcast CP subsystem status via Consul dashboard.
2. Verify lock TTL or perform manual force-release through Hazelcast Management Center if available.

### Clearing Idempotency State
If a re-run of a specific `messageId` is required during recovery:
1. Delete key from Cassandra idempotency store:
   `DELETE FROM probe.idempotency WHERE message_id = 'MSG-XXX';`
2. Re-queue message to `probe.commands` SQS queue.