# Test Plan: PRB-4821 Hazelcast Lock & SQS Idempotency for SNMP Walks

## Summary
Validates that concurrent SNMP walk executions targeting the same network device (`deviceId`) are serialized via Hazelcast distributed locks, duplicate SQS messages are identified and skipped, and processing metrics are properly recorded.

## Test Cases

1. **Concurrent Message Handling**
   - Send two identical SQS commands for `DEV-ASR1001-01` simultaneously.
   - Verify that one worker acquires `snmp-lock-DEV-ASR1001-01` while the other waits or safely skips execution.

2. **SQS Message Idempotency Check**
   - Send a message with `messageId=MSG-99101` twice.
   - Ensure the second invocation triggers `probe.snmp.idempotent` metric increment and skips execution.

3. **SQS Visibility Timeout Extension**
   - Verify visibility timeout extension of 120 seconds is requested on SQS before executing heavy SNMP walks.

4. **Cassandra Idempotency Record Verification**
   - Inspect `probe.idempotency` table in Cassandra to verify `messageId` persists after completion.

## Staging Setup
- **Queue Name**: `probe.commands`
- **Cassandra Table**: `probe.idempotency`
- **Actuator Endpoint**: `/actuator/metrics/probe.snmp.idempotent`

## Pass Criteria
- Zero concurrent execution overlaps for the same `deviceId`.
- Micrometer counter `probe.snmp.idempotent` increments on duplicate messages.
- Idempotency record stored in Cassandra.