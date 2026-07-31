# Runbook – ENR‑77402 Idempotent SQS Consumer Validation

## Purpose
Provide operators with a step‑by‑step guide to verify that the **Data‑Enricher** SQS consumer maintains idempotency and correctly handles visibility‑timeout extensions in the staging environment.

## Prerequisites
- Access to the `staging` Kubernetes namespace.
- `aws` CLI configured for the `us-east-1` account.
- `cqlsh` access to the Cassandra cluster.
- Micrometer metrics endpoint reachable from the operator workstation.

## Procedure
1. **Deploy the latest Enricher image**
   ```bash
   kubectl -n staging set image deployment/data-enricher data-enricher=repo/data-enricher:latest
   ```
2. **Publish test messages**
   ```bash
   aws sqs send-message-batch --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline \
       --entries file://test-messages.json
   ```
   *`test-messages.json`* contains ten messages, two of which share the same `messageId`.
3. **Wait for processing** – allow up to 120 seconds for the consumer to finish.
4. **Validate Cassandra**
   ```bash
   cqlsh -e "SELECT message_id, count(*) FROM probe.results WHERE message_id='msg-dup';"
   ```
   Expected result: count = 1.
5. **Check CloudWatch logs**
   Search for the pattern `Extending visibility timeout for message` in the Log Group `/aws/ecs/data-enricher`.
6. **Verify metrics**
   ```bash
   curl -s http://staging-enrich.internal:8080/actuator/metrics/enrichment.failures | jq .
   ```
   The `count` field should be `0` for a clean run.
7. **Rollback (if needed)**
   ```bash
   kubectl -n staging rollout undo deployment/data-enricher
   ```

## Post‑Run Cleanup
- Purge test rows:
  ```bash
  cqlsh -e "DELETE FROM probe.results WHERE message_id IN ('msg-1','msg-2',...);"
  ```
- Delete the test batch from the queue:
  ```bash
  aws sqs purge-queue --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline
  ```

## Owner
- **Team**: Data‑Enricher
- **Primary Contact**: eng‑enricher@netatlas.internal

---
*Runbook generated for ticket ENR‑77402.*
