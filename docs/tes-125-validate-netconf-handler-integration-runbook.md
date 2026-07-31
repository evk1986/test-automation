# Runbook – TES‑125 – NETCONF Handler Integration Validation

## Purpose
Provide operators with a repeatable procedure to execute a staging batch through the NETCONF handler, verify successful processing, and safely roll back if anomalies are detected.

## Prerequisites
- Access to the **staging** AWS account (us‑east‑1).
- IAM role with permissions for SQS, Cassandra, and CloudWatch.
- `awscli` configured for the staging profile.
- `cqlsh` installed and network access to the Cassandra cluster.
- Micrometer metrics endpoint reachable (`http://staging-probe.internal:8080/actuator/metrics`).

## Steps
1. **Publish the Batch Message**
   ```bash
   aws sqs send-message \
       --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands \
       --message-body '{"protocol":"NETCONF","batchId":"BATCH-PRB-20240523-USE1-01","deviceIds":["dev-001","dev-002",...]}'
   ```
2. **Confirm Handler Reception**
   - Check the probe service logs (`kubectl logs -l app=probe -c probe`). Look for `netconf.batch.processed` log entry.
   - Verify the Micrometer counter `netconf.batch.processed` increased by `1` via:
     ```bash
     curl -s http://staging-probe.internal:8080/actuator/metrics/netconf.batch.processed | jq .
     ```
3. **Validate Cassandra Writes**
   ```bash
   cqlsh> SELECT count(*) FROM netatlas.probe.results WHERE batch_id='BATCH-PRB-20240523-USE1-01';
   ```
   - The count should equal the number of devices defined for the batch.
   - Spot‑check a few rows to ensure `status='SUCCESS'`.
4. **Idempotency Check**
   - Re‑run step 1 with the same payload.
   - Re‑query the row count; it must remain unchanged.
5. **DLQ & Failure Metrics**
   - Pull DLQ metric:
     ```bash
     curl -s http://staging-probe.internal:8080/actuator/metrics/platform.dlq | jq .
     ```
   - Ensure the value is `0`.
   - Verify `netconf.failure` counter has not increased.
6. **Rollback (if needed)**
   - If duplicate rows or failures are observed, delete the batch rows:
     ```bash
     cqlsh> DELETE FROM netatlas.probe.results WHERE batch_id='BATCH-PRB-20240523-USE1-01';
     ```
   - Purge the message from the queue (or move to DLQ) using `aws sqs purge-queue`.

## Post‑Run Cleanup
- Ensure no stray messages remain in `probe.commands` for the test batch.
- Document observed metric values in the run log.
- Notify the QA team that the validation has completed.

---
*Runbook authored for ticket **TES‑125** – 2026‑06‑05.*