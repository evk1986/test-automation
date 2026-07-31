# Summary
Validate that the **Data‑Enricher** consumer on the `enrich.pipeline` SQS queue is idempotent and correctly extends the visibility timeout when processing messages.

# Test Cases
1. **Duplicate Message Handling** – Send two messages with the same `messageId`. Verify only one row is written to the `probe.results` Cassandra table.
2. **Visibility Timeout Extension** – Simulate a long‑running enrichment and confirm that the consumer logs a visibility‑timeout‑extension entry.
3. **Metric Verification** – After processing, query the Micrometer `/actuator/metrics/enrichment.failures` endpoint and ensure the failure counter reflects only genuine errors.

# Staging Setup
- **Queue**: `enrich.pipeline` (standard SQS, DLQ: `platform.results.dlq`).
- **Cassandra Table**: `probe.results` (primary key `message_id`).
- **Actuator Endpoint**: `http://staging-enrich.internal:8080/actuator/metrics/enrichment.failures`.
- **Environment**: `staging` (region `us-east-1`).

# Pass Criteria
- No duplicate rows exist for the same `messageId` in `probe.results`.
- CloudWatch logs contain entries like `Extending visibility timeout for message <id>` for each processed message.
- Failure counter metric matches the number of intentionally induced errors (zero for the happy‑path run).

# Execution Steps
1. Deploy the latest `Data‑Enricher` artifact to the staging namespace.
2. Use the provided test harness to publish a batch of 10 messages, including 2 duplicates.
3. Wait for the consumer to finish (max 2 minutes).
4. Query Cassandra and CloudWatch, then verify metrics.

# Cleanup
- Delete test messages from the queue.
- Truncate the `probe.results` table entries created by the test.
