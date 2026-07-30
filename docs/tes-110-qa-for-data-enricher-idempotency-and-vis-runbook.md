# Runbook – Data‑Enricher Idempotency & Visibility Timeout Verification (ENR‑77402)

## Purpose
Provide a repeatable procedure to validate that the Data‑Enricher service handles duplicate SQS messages without creating duplicate Cassandra rows and correctly extends visibility timeout when processing exceeds the initial window.

## Prerequisites
- Access to the **staging** environment (`staging-enrich.internal`).
- AWS IAM role with permissions to read/write `enrich.pipeline` and its DLQ.
- Cassandra client configured for keyspace `netatlas`.
- `awscli` and `cqlsh` installed locally.

## Steps
1. **Prepare Test Data**
   ```bash
   aws sqs send-message --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline \
       --message-body '{"messageId":"test-dup-001","payload":{...}}'
   aws sqs send-message --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline \
       --message-body '{"messageId":"test-dup-001","payload":{...}}'
   ```
2. **Trigger Processing** – Ensure the Data‑Enricher pod is running. The service will automatically consume the messages.
3. **Validate Idempotency**
   ```bash
   cqlsh> SELECT count(*) FROM enrich.results WHERE id='test-dup-001';
   ```
   Expected result: `count = 1`.
4. **Check Visibility Timeout**
   - Introduce an artificial delay in the handler (e.g., `Thread.sleep(20000)`).
   - After processing, view CloudWatch metric:
   ```bash
   aws cloudwatch get-metric-statistics --namespace AWS/SQS \
       --metric-name ApproximateAgeOfOldestMessage --dimensions Name=QueueName,Value=enrich.pipeline \
       --statistics Average --period 60 --start-time $(date -u -d '-5 minutes' +%Y-%m-%dT%H:%M:%SZ) \
       --end-time $(date -u +%Y-%m-%dT%H:%M:%SZ)
   ```
   Verify the age never exceeds the configured 30‑second visibility timeout.
5. **Cleanup** – Delete test rows and purge the queue.
   ```bash
   cqlsh> DELETE FROM enrich.results WHERE id='test-dup-001';
   aws sqs purge-queue --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline
   ```

## Rollback
If duplicate rows are found, stop the Data‑Enricher pod, clear the `enrich.results` table for the affected `messageId`s, and redeploy the service with the latest idempotency fix.

## Monitoring
- **Prometheus**: `sqs_visibility_timeout_seconds` gauge.
- **CloudWatch**: `SQSVisibilityTimeoutExtensions` counter.
- **Logs**: Look for `Duplicate enrichment message ignored` entries.

---
*Document version: 1.0 – 2026‑07‑30*
