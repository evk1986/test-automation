# Runbook – Data‑Enricher Idempotency Key Management & Visibility‑Timeout

**Ticket:** TES‑174  
**Feature:** ENR‑77402 – Idempotency & Timeout Runbook  
**Owner:** Data‑Enricher Lead

---

## 1. Overview
The Data‑Enricher now stores an **idempotency key** for every incoming enrichment message. The key is derived from the combination of:
- Normalized record ID
- Device ID
- Processing timestamp (truncated to seconds)

The key is persisted in the Cassandra table **`enrichment_result`** (primary key `idempotency_key`). Subsequent deliveries of the same message are detected via a simple `SELECT` and are ignored, guaranteeing exactly‑once semantics downstream.

### 1.1 Cassandra Table Layout
```cql
CREATE TABLE IF NOT EXISTS enrichment_result (
    idempotency_key text PRIMARY KEY,
    device_id        text,
    raw_payload      text,
    processed_at    timestamp
);
```
- **idempotency_key** – SHA‑256 hash of `recordId|deviceId|epochSecond`.
- **processed_at** – UTC timestamp used for TTL cleanup (default 30 days).

## 2. Visibility‑Timeout Monitoring
Each SQS message from `enrich.pipeline` has a **visibility timeout** of **30 seconds**. The platform emits the Prometheus metric `aws_sqs_visibility_timeout_seconds` with labels `queue="enrich.pipeline"`.

### 2.1 Verify Settings in Staging
```bash
aws sqs get-queue-attributes \
    --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline \
    --attribute-names VisibilityTimeout
```
Expected output: `VisibilityTimeout: 30`

### 2.2 Dashboard
- **Dashboard URL:** https://monitoring.internal/dashboards/data-enricher-idempotency
- **Key panels:**
  - `enricher.idempotent.success`
  - `enricher.idempotent.duplicate`
  - `aws_sqs_visibility_timeout_seconds{queue="enrich.pipeline"}`

If the visibility‑timeout metric spikes above **30 s** for more than **5 minutes**, trigger the alert `SQS_VisibilityTimeout_Exceeded`.

## 3. TL;DR – Draining the DLQ (`platform.results.dlq`)
When duplicate messages accumulate in the DLQ, operators can safely purge them because they have already been processed.

### 3.1 Prerequisites
- Access to the `platform.results.dlq` SQS queue (IAM role `ops-data-enricher`).
- `awscli` version ≥ 2.7 installed.
- Confirmation that the duplicate count is **> 0** via the DLQ metric `aws_sqs_approximate_number_of_messages_visible`.

### 3.2 Drain Script
```bash
#!/usr/bin/env bash
set -euo pipefail

QUEUE_URL="https://sqs.us-east-1.amazonaws.com/123456789012/platform.results.dlq"

# Fetch and delete messages in batches of 10 (max allowed by SQS)
while true; do
  MSGS=$(aws sqs receive-message \
    --queue-url "$QUEUE_URL" \
    --max-number-of-messages 10 \
    --wait-time-seconds 2 \
    --output json)

  COUNT=$(echo "$MSGS" | jq '.Messages | length')
  if [[ "$COUNT" -eq 0 ]]; then
    echo "DLQ is empty – drain complete."
    break
  fi

  RECEIPT_HANDLES=$(echo "$MSGS" | jq -r '.Messages[].ReceiptHandle')
  for RH in $RECEIPT_HANDLES; do
    aws sqs delete-message --queue-url "$QUEUE_URL" --receipt-handle "$RH"
  done
  echo "Deleted $COUNT messages…"
  sleep 1
 done
```
- Save as `drain-dlq.sh` and run with `bash drain-dlq.sh`.
- The script logs each batch; successful completion is recorded in CloudWatch under `DataEnricherDLQDrain`.

### 3.3 Post‑Drain Verification
```bash
aws sqs get-queue-attributes \
    --queue-url "$QUEUE_URL" \
    --attribute-names ApproximateNumberOfMessages
```
The attribute should return `0`.

## 4. Incident Escalation
If the DLQ count does **not** drop after running the script, or if the visibility‑timeout alert fires repeatedly:
1. Open a ticket in ServiceNow with label **ENR‑DLQ‑FAIL**.
2. Capture the latest logs from the Data‑Enricher pod (`kubectl logs -l app=data-enricher`).
3. Notify the Data‑Enricher lead (Slack channel `#netatlas-enrich`).

## 5. References
- **Metric Dashboard:** https://monitoring.internal/dashboards/data-enricher-idempotency
- **Cassandra Schema Docs:** https://confluence.internal/pages/viewpage.action?pageId=12345678
- **SQS Visibility‑Timeout Docs:** https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html

---
*Approved by:* Jane Doe (Data‑Enricher Lead) – 2026‑06‑30
*Published under:* `platform/operations/runbooks`
