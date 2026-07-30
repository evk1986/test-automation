# Runbook – DLQ Monitoring & Automated Drain for `probe.commands` (ORCH‑882)

**Owner**: Fleet‑Orchestrator Team

## Purpose
Provide operators with a repeatable procedure to monitor the dead‑letter queue (DLQ) for `probe.commands`, reset visibility timeouts, and drain stuck messages back into the processing pipeline.

## Prerequisites
- Access to the **Orchestrate** service pod (`kubectl exec -it <pod> -- /bin/bash`).
- `curl` installed inside the pod.
- IAM role with permission to call the internal **DLQ Drain** REST endpoint.
- Observability team has approved the `dlq.*` Prometheus metrics.

## Step‑by‑Step Procedure
1. **Check DLQ Depth**
   ```bash
   curl -s http://localhost:8080/actuator/metrics/dlq.messages.remaining | jq .
   ```
   Record the `measurements[0].value` as `remaining`.
2. **Inspect Sample Message**
   ```bash
   aws sqs receive-message --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands.dlq \
       --max-number-of-messages 1 --visibility-timeout 0
   ```
   Verify the payload contains a `jobId` field.
3. **Reset Visibility Timeout (if needed)**
   ```bash
   curl -X POST http://localhost:8080/api/v1/orchestrate/dlq/visibility-reset \
       -H "Content-Type: application/json" \
       -d '{"queue":"probe.commands.dlq","visibilityTimeout":30}'
   ```
   Expected response: `{"status":"OK","resetTo":30}`.
4. **Run Automated Drain**
   ```bash
   curl -X POST http://localhost:8080/api/v1/orchestrate/dlq/drain \
       -H "Content-Type: application/json" \
       -d '{"sourceQueue":"probe.commands.dlq","targetQueue":"probe.commands"}'
   ```
   The service will:
   - Pull messages from the DLQ.
   - Call `DlqDrainService.processDlqMessage` for each.
   - Re‑publish to the main queue.
   - Record an audit entry in Cassandra (`dlq_audit`).
5. **Validate Drain Success**
   ```bash
   curl -s http://localhost:8080/actuator/metrics/dlq.messages.remaining | jq .
   curl -s http://localhost:8080/actuator/metrics/dlq.messages.drain.count | jq .
   ```
   `remaining` should be `0`; `drain.count` should equal the number of processed messages.
6. **Link to Architectural Decision Record**
   - ADR‑0043: *DLQ Visibility Management & Automated Drain* is stored in Confluence under `https://confluence.internal/adr/0043`.
   - Add the ADR link to the top of this runbook for future reference.

## Troubleshooting
| Symptom | Likely Cause | Action |
|---------|--------------|--------|
| No messages re‑queued | Message missing `jobId` field | Review raw payload (step 2) and use the **Manual Re‑queue** API (`/api/v1/orchestrate/dlq/manual-requeue`). |
| Drain hangs > 5 min | SQS rate‑limit or IAM permission error | Check CloudWatch logs for `AccessDenied` and verify IAM role attached to the pod. |
| Metrics unchanged | Prometheus scrape failure | Ensure `orchestrate` pod annotation `prometheus.io/scrape: "true"` is present and reload Prometheus config. |

## Post‑Run Cleanup
- Delete any temporary test messages from the main queue if they were injected for verification.
- Archive the `dlq_audit` entries older than 30 days via the scheduled cleanup job.

---
*Runbook version 1.0 – created 2026‑07‑30, reviewed by Observability team.*
