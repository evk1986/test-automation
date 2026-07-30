# Runbook – NETCONF Handler Integration Validation (TES‑99)

## Purpose
Provide operators with a repeatable procedure to validate the NETCONF subtree handler for Cisco IOS‑XR NCS devices after a new image rollout.

## Prerequisites
- Access to the **staging** Kubernetes cluster (`kubectl config use-context staging`)
- AWS credentials with permission to read SQS queues and write to CloudWatch Logs
- `cqlsh` installed and network access to the Cassandra seed node (`cassandra-staging.internal:9042`)
- Micrometer metrics endpoint reachable from the operator workstation

## Steps
1. **Deploy Image**
   ```bash
   helm upgrade device-probe ./helm/device-probe \
     --set image.tag=2.7.7-netconf-v1 \
     --namespace staging
   ```
2. **Verify Pods** – Ensure all `device-probe` pods are `Ready`.
   ```bash
   kubectl get pods -n staging -l app=device-probe
   ```
3. **Trigger Batch**
   ```bash
   curl -X POST \
        http://staging-orchestrator.internal/api/v1/orchestrate/batch/BATCH-PRB-20240523-USE1-01 \
        -H "Content-Type: application/json"
   ```
4. **Monitor SQS** – Use the AWS CLI to poll the queues for the duration of the batch (≈10 min).
   ```bash
   aws sqs receive-message --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands --max-number-of-messages 10
   ```
5. **Validate Cassandra Writes**
   ```bash
   cqlsh> SELECT device_id, interface_name FROM probe.results WHERE batch_id='BATCH-PRB-20240523-USE1-01' ALLOW FILTERING;
   ```
   Confirm that the row count matches the expected device count.
6. **Check Metrics**
   ```bash
   curl http://staging-probe.internal:8080/actuator/metrics/probe.protocol.failures?tag=protocol:netconf&tag=deviceFamily:iosxr_ncs
   ```
   The `value` field must be `0`.
7. **DLQ Scan**
   ```bash
   aws sqs get-queue-attributes --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/platform.results.dlq --attribute-names ApproximateNumberOfMessages
   ```
   The result should be `0`.

## Verification
- All steps complete without errors.
- Row count in Cassandra equals the number of devices in the batch (e.g., 20 000).
- Metric `probe.protocol.failures` is zero.
- No messages appear in the DLQ.

## Rollback
If any step fails:
1. Roll back the Helm release:
   ```bash
   helm rollback device-probe <previous‑revision>
   ```
2. Delete the batch via orchestrator:
   ```bash
   curl -X DELETE http://staging-orchestrator.internal/api/v1/orchestrate/batch/BATCH-PRB-20240523-USE1-01
   ```
3. Purge any partially processed messages from `probe.commands` and `normalize.ingest` using the AWS console.

---
*Runbook authored for ticket **PRB‑4821** – 2026‑06‑04.*
