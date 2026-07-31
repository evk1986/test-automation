# Runbook – Draining `probe.commands` DLQ after NETCONF Handler Rollout (PRB‑4821)

## Purpose
After the NETCONF handler (`DocsRunbooksDocsAdrHandler`) is promoted to production, any NETCONF jobs that exceed the retry budget or encounter unrecoverable errors are routed to the dead‑letter queue `platform.results.dlq`. This runbook describes the safe procedure to inspect, reprocess, or purge those messages.

## Prerequisites
- Access to the **prod‑use1** AWS account with the `netatlas-ops` IAM role (assumed via HashiCorp Vault).
- `awscli` version >= 2.7 installed on the workstation.
- Permissions: `sqs:ReceiveMessage`, `sqs:DeleteMessage`, `sqs:SendMessage`, `sqs:GetQueueAttributes` on both `probe.commands` and `platform.results.dlq`.
- The **NETCONF rollout** ticket **PRB‑4821** must be in *Implementation Complete* state.

## Steps
1. **Identify the DLQ URL**
   ```bash
   aws sqs get-queue-url --queue-name platform.results.dlq --region us-east-1
   ```
   Record the `QueueUrl` value (e.g., `https://sqs.us-east-1.amazonaws.com/123456789012/platform.results.dlq`).

2. **Pull a Sample of Messages**
   ```bash
   aws sqs receive-message \
       --queue-url $DLQ_URL \
       --max-number-of-messages 10 \
       --visibility-timeout 300 \
       --attribute-names All \
       --message-attribute-names All \
       --region us-east-1
   ```
   Review the `Body` field – it should contain a JSON representation of `ProbeJobMessage` with `protocol":"NETCONF"`.

3. **Validate Message Content**
   - Ensure `jobId` follows the pattern `JOB‑NETCONF‑<digits>`.
   - Confirm `attemptCount` is **≥** the configured retry limit (default 5).
   - Verify `lastErrorMessage` is not a transient network glitch (e.g., *"connection timeout"*). If it is, consider a manual retry instead of purge.

4. **Reset Visibility Timeout (if re‑processing)**
   If you decide to re‑process a message, extend its visibility to give the handler enough time:
   ```bash
   aws sqs change-message-visibility \
       --queue-url $DLQ_URL \
       --receipt-handle <ReceiptHandle> \
       --visibility-timeout 900 \
       --region us-east-1
   ```
   This mirrors the runtime behaviour of the handler where it calls `ChangeMessageVisibility` after a successful status update.

5. **Re‑publish to the Main Queue**
   ```bash
   aws sqs send-message \
       --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands \
       --message-body "<original‑body‑json>" \
       --region us-east-1
   ```
   The message will be picked up by `DocsRunbooksDocsAdrHandler` on its next poll.

6. **Delete the Processed DLQ Message**
   ```bash
   aws sqs delete-message \
       --queue-url $DLQ_URL \
       --receipt-handle <ReceiptHandle> \
       --region us-east-1
   ```
   Deleting prevents the same message from being re‑delivered.

7. **Metrics Verification**
   - Query Prometheus for `netconf.handler.failure_total` before and after the re‑process.
   - Ensure the counter does **not** increase after a successful re‑process (the failure metric should only reflect genuine failures).

8. **Audit Log Entry**
   Record the following in the ticket **PRB‑4821**:
   - Timestamp of the operation.
   - Number of messages inspected, re‑processed, and purged.
   - Any anomalies (e.g., malformed payloads).

## Rollback
If an unexpected surge of re‑processed messages overwhelms downstream services:
1. Stop re‑publishing new messages.
2. Purge the `probe.commands` queue temporarily using:
   ```bash
   aws sqs purge-queue --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands
   ```
3. Notify the architecture lead and reopen the ticket for further analysis.

---
*Runbook authored by the Device‑Probe team, linked to ticket **PRB‑4821** (2026‑06‑09).*