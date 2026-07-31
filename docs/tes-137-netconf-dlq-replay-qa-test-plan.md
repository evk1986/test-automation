# Summary
Validate that the NETCONF DLQ replay service processes failed messages without creating duplicate NETCONF sessions, correctly releases Hazelcast locks, and increments the `netconf.dlq.replay.processed` metric.

# Test Cases
1. **Successful replay** – A DLQ message for a FAILED job is consumed, lock is acquired, processor is invoked, metric increments, and the message is deleted.
2. **Duplicate session avoidance** – If a job status is SUCCESS, the service skips processing, ensuring no new NETCONF session is started.
3. **Lock contention** – When the lock cannot be acquired, the message remains in DLQ for a later attempt.
4. **Error handling** – Exceptions during processing do not delete the message, allowing retry.

# Staging Setup
- **SQS DLQ**: `platform.results.dlq` (configured in `NetconfDlqReplayService.DLQ_QUEUE_URL`).
- **Cassandra table**: `probe_job` with columns `id`, `device_id`, `status`, `attempt_count`.
- **Hazelcast**: CP subsystem lock named `netconf-lock-{deviceId}`.
- **Actuator endpoint**: `GET /actuator/metrics/netconf.dlq.replay.processed`.

# Pass Criteria
- All test cases pass in the unit test suite.
- Metric `netconf.dlq.replay.processed` shows an increment for each successfully replayed message.
- No duplicate NETCONF sessions are observed in logs or mock verifications.
- Locks are always released after processing.
