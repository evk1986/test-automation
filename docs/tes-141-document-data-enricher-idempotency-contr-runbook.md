# Runbook – ENR‑77402 – Idempotency‑Key Contract & Visibility‑Timeout Tuning

## Purpose
Provide operators with step‑by‑step instructions to verify the idempotency‑key contract and to adjust the SQS visibility timeout for the enrichment pipeline.

## Prerequisites
- Access to the `staging` Kubernetes namespace.
- `kubectl` configured for the target cluster.
- AWS CLI with permissions to modify SQS queue attributes.

## Steps

### 1. Verify Idempotency‑Key Table
```bash
cqlsh staging-db.internal -e "
SELECT count(*) FROM enrich.idempotency_key;
"
```
Confirm the count matches expectations from recent runs.

### 2. Test REST Endpoint
```bash
curl -s -o /dev/null -w "%{http_code}" http://staging-enrich.internal:8080/api/v1/enrich/idempotency/info
```
Expect HTTP 200. The response body should contain “Idempotency keys stored: X”.

### 3. Tune Visibility Timeout
The enrichment service reads from `enrich.pipeline`. To change the timeout:
```bash
aws sqs set-queue-attributes \
    --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline \
    --attributes VisibilityTimeout=180
```
- Recommended values: 60 s for low latency, up to 300 s for large payloads.
- After change, restart the `data-enricher` pod to pick up the new setting.

### 4. Verify Change
```bash
aws sqs get-queue-attributes \
    --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline \
    --attribute-names VisibilityTimeout
```
The returned value should match the setting applied in step 3.

### 5. Rollback (if needed)
```bash
aws sqs set-queue-attributes \
    --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline \
    --attributes VisibilityTimeout=30
```
Then redeploy the service.

## Monitoring
- Micrometer metric `sqs.visibility.timeout` should reflect the configured value.
- Alert on `enrich.idempotency.key.count` deviation > 10 % over 5 minutes.

## Documentation Links
- ADR ADR‑0045 (see `docs/adr-0045.md`)
- Confluence page “Data‑Enricher Async API”.
