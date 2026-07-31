# Test Plan – TES-165 Distributed Lock & Idempotency

## Summary
This test plan validates the lock acquisition flow, idempotency key handling, and DLQ behaviour introduced for the Device‑Probe service. It exercises the `DistributedLockAcquisitionHandler` and `DistributedLockService` against a staging Hazelcast cluster and SQS queue.

## Test Cases
1. **Successful lock acquisition** – Send a `LockRequestMessage` to `probe.commands`. Verify the handler calls `DistributedLockService.acquireLock` and the lock is held in Hazelcast.
2. **Lock contention** – Pre‑acquire the same lock key in Hazelcast, then send the message. Verify the handler logs a warning and the message is retried according to the SQS visibility timeout.
3. **Idempotency key schema** – Confirm that the generated lock key follows the pattern `lock:{jobId}:{lockId}` and matches the idempotency schema documented in the runbook.
4. **DLQ drain** – Simulate a failure to acquire the lock after three retries. Verify the message lands in `platform.results.dlq` and can be re‑processed manually.
5. **Graceful interruption** – Force an `InterruptedException` in `DistributedLockService`. Verify the method returns `false` and the thread interrupt flag is restored.

## Staging Setup
- **SQS queue**: `probe.commands` (standard) and dead‑letter queue `platform.results.dlq`.
- **Hazelcast**: Staging cluster reachable via `hazelcast-client-staging.yml`.
- **Cassandra tables**: No persistence required for this slice; only lock state is in Hazelcast.
- **Actuator endpoint**: `http://staging-probe.internal.netatlas.com/actuator/health` should report `UP` before tests.

## Pass Criteria
- All test cases execute without uncaught exceptions.
- Locks are correctly created and released in Hazelcast.
- Messages that cannot acquire a lock after three attempts appear in the DLQ.
- Log entries contain the expected lock key format.

---
*Prepared by the junior backend contractor for ticket TES‑165.*
