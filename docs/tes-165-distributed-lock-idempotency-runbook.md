# Runbook – Distributed Lock & Idempotency (MIGR‑3310)

## Overview
The Device‑Probe service uses Hazelcast distributed locks to guarantee exclusive access to device‑specific resources during a polling batch. Each lock is tied to an **idempotency key** that encodes the job and lock identifiers. This runbook describes the acquisition flow, visibility‑timeout handling, and the procedure for draining the dead‑letter queue (DLQ) when locks cannot be obtained.

## Lock Acquisition Flow
1. **Message ingestion** – A `LockRequestMessage` arrives on the `probe.commands` SQS queue.
2. **Handler invocation** – `DistributedLockAcquisitionHandler.handle()` extracts `jobId`, `deviceId` and `lockId`.
3. **Key generation** – The handler builds the lock key:
   ```
   lockKey = "lock:" + jobId + ":" + lockId
   ```
4. **Hazelcast lock request** – `DistributedLockService.acquireLock(lockKey)` obtains an `ILock` from the Hazelcast instance and attempts `tryLock(5, TimeUnit.SECONDS)`.
5. **Success path** – If the lock is granted, processing continues downstream (e.g., Data‑Enricher).
6. **Failure path** – If the lock is not granted, the handler logs a warning. The SQS message visibility timeout is left unchanged, allowing the next retry attempt.

## Idempotency Key Schema
| Component | Description | Example |
|-----------|-------------|---------|
| Prefix    | Fixed string `lock` to identify lock keys. | `lock` |
| Job ID    | The batch identifier from `ProbeJob`. | `JOB-20240523-USE1-01` |
| Lock ID   | Unique identifier for the lock scope (e.g., device‑specific). | `netconf-config` |

**Full key**: `lock:JOB-20240523-USE1-01:netconf-config`

## Visibility‑Timeout Reset Steps
- The handler does **not** modify the SQS visibility timeout on each retry; the default timeout (30 seconds) is sufficient for the 5‑second lock attempt.
- If a lock remains unavailable after three retries, the message is automatically routed to the DLQ `platform.results.dlq`.

## DLQ Drain Procedure
1. Navigate to the AWS SQS console for `platform.results.dlq`.
2. Pull messages using the **Receive Message** action with a batch size of 10.
3. For each message, inspect the `lockKey` and determine the cause of contention (e.g., stale lock, zombie process).
4. Manually release the lock in Hazelcast if it is orphaned:
   ```java
   hazelcastInstance.getLock(lockKey).forceUnlock();
   ```
5. Re‑publish the message to `probe.commands` using the AWS CLI or console **Send Message**.
6. Verify that the message is processed successfully in the next poll.

## Troubleshooting
- **Repeated lock failures** – Check Hazelcast cluster health; look for network partitions or node failures.
- **Stale locks** – Ensure that any long‑running job releases its lock on completion or on exception via a `finally` block.
- **Visibility timeout too short** – Increase the SQS queue setting if lock acquisition consistently exceeds the timeout.
- **DLQ growth** – Review batch sizing in `Fleet‑Orchestrator` and adjust retry budgets.

## References
- ADR‑0051 – Sequence diagram linking Device‑Probe lock request → Hazelcast → Data‑Enricher idempotency check.
- Platform wiki page: *Distributed‑Lock‑Design* (linked from onboarding checklist).
- AWS SQS documentation for dead‑letter queues.

---
*Runbook authored for ticket MIGR‑3310.*
