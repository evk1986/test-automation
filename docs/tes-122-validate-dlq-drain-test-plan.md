# Summary
Validate that the DLQ drain endpoint for `probe.commands` processes poisoned messages within the SLA and that metrics are recorded correctly.

# Test Cases
1. **Drain 5k poisoned messages** – invoke `/api/v1/orchestrate/dlq/drain?maxMessages=1000` repeatedly until the DLQ is empty. Verify total processed count equals 5,000.
2. **Metric verification** – ensure `platform.dlq.probe_commands.drain` timer records duration and `platform.dlq.probe_commands.processed` counter increments by the number of messages drained.
3. **Cassandra consistency** – after drain, query `probe.dlq_audit` table with `CONSISTENCY LOCAL_QUORUM` and confirm audit rows match processed messages.
4. **SLA enforcement** – each drain call must complete within 2 seconds.

# Staging Setup
- **Queue names**: `probe.commands` (main), `probe.commands.dlq` (DLQ) configured via `aws.sqs.probe-commands.url` and `aws.sqs.dlq.probe-commands.url`.
- **Cassandra table**: `probe.dlq_audit` (primary key: message_id, columns: processed_at, status).
- **Actuator endpoint**: `/actuator/metrics/platform.dlq.probe_commands.drain` and `/actuator/metrics/platform.dlq.probe_commands.processed`.
- **Environment**: `staging` with Hazelcast disabled for deterministic behavior.

# Pass Criteria
- All 5,000 messages are re‑published to the main queue and removed from the DLQ.
- Micrometer metrics show a total processed count of 5,000 and each drain call duration ≤ 2 s.
- Cassandra audit rows count = 5,000 with `CONSISTENCY LOCAL_QUORUM`.
