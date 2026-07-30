# Summary
Execute an end‑to‑end replay of SQS messages through the Data‑Enricher pipeline to verify idempotent handling and visibility‑timeout behavior for batch **POLL‑RAPID‑77402**.

# Test Cases
1. **Duplicate Message Replay** – Send the same `EnrichMessage` payload twice to the `enrich.pipeline` queue and assert that only one `EnrichmentResult` row exists in Cassandra.
2. **Visibility Timeout Extension** – Configure the SQS queue with a 30‑second visibility timeout, process a message that deliberately sleeps 20 seconds, and verify that the timeout is extended (CloudWatch metric `ApproximateAgeOfOldestMessage` remains < 30s).
3. **SLA Completion** – Run the replay for 10,000 messages and ensure the total execution time does not exceed the defined SLA of 5 minutes.

# Staging Setup
- **Queue Names**: `enrich.pipeline` (standard), DLQ `platform.results.dlq`.
- **Cassandra Table**: `enrich.results` with primary key `id` (message‑id).
- **Actuator Endpoint**: `http://staging-enrich.internal:8080/actuator/metrics/sqs.visibilityTimeout`
- **Metrics**: CloudWatch metric `SQSVisibilityTimeoutExtensions` must increment for each extension.

# Pass Criteria
- No duplicate rows for the same `messageId` in `enrich.results`.
- Visibility timeout extensions are logged and the metric shows at least one increment.
- All test cases complete within the 5‑minute SLA.
