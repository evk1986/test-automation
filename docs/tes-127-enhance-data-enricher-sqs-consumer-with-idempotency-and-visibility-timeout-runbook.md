# Runbook – ENR‑77402 – Data‑Enricher SQS Consumer Enhancements

## Purpose
This runbook documents the operational steps required to monitor, troubleshoot, and roll back the idempotency‑key and visibility‑timeout changes deployed for the Data‑Enricher service (ticket TES‑127).

## Prerequisites
- Access to the **prod‑use1** Kubernetes namespace where `data-enricher` pods run.
- `kubectl` configured with the appropriate IAM role.
- Access to the Prometheus UI and Grafana dashboards for the `enricher` service.
- Ability to query Cassandra (cqlsh) and AWS SQS (awscli).

## Normal Operation Checklist
1. **Metric Verification**
   - Open `https://grafana.internal.netatlas/dashboard/enricher`.
   - Confirm the panel *Enricher Failure Count* shows a low, stable value.
   - Verify the metric `enricher.failures` is present via `curl https://<pod-ip>:8080/actuator/metrics/enricher.failures`.

2. **Idempotency Store Health**
   - Run `cqlsh -e "SELECT count(*) FROM enricher.idempotency_keys WHERE ttl > 0;"`.
   - Ensure the row count grows proportionally with the ingestion rate and does not exceed the configured TTL (default 7 days).

3. **Visibility Timeout Monitoring**
   - In CloudWatch, watch the `SQS/ChangeMessageVisibility` metric for the `enrich.pipeline` queue. Spikes should correspond to the batch size of the poller.

## Incident Response
### Symptom: Duplicate Enrichment Detected
1. Check the `enricher.idempotency_keys` table for the offending `message_id`.
2. If the key is missing, it indicates a write failure; investigate Cassandra logs and node health.
3. If the key exists, confirm that the handler logged *Skipping duplicate* – no further action required.

### Symptom: Messages Stuck in Queue (Visibility Timeout Not Reset)
1. Identify the message via `aws sqs receive-message --queue-url <url> --max-number-of-messages 1 --visibility-timeout 0`.
2. Verify the `ApproximateReceiveCount` is > 1.
3. Review the pod logs for *Failed to change visibility timeout* warnings.
4. If the SQS client is failing, restart the `data-enricher` deployment to re‑initialize the SDK credentials.

### Symptom: Unexpected Failure Counter Increase
1. Query the metric with tags: `curl https://<pod-ip>:8080/actuator/metrics/enricher.failures?tag=protocol:SNMP&tag=region:us-west-2`.
2. Correlate the timestamp with the service logs to locate the exception stack trace.
3. If the exception is transient (e.g., downstream DB timeout), consider increasing the retry budget in `policy/RetryBudgetPolicy`.

## Rollback Procedure
1. **Deploy previous image**
   ```bash
   kubectl set image deployment/data-enricher data-enricher=repo/data-enricher:1.4.3 \
       --namespace=prod-use1
   ```
2. **Disable new metrics** – remove the `enricher.failures` counter registration from the code (if necessary) and redeploy.
3. **Clean up idempotency keys** (optional):
   ```cql
   TRUNCATE enricher.idempotency_keys;
   ```
   This is safe because the previous version does not rely on the table.
4. Verify that the service processes messages without the visibility‑timeout extension and that the old processing path works as expected.

## Documentation Links
- [AWS SQS Visibility Timeout Docs](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html)
- [Micrometer Counter Guide](https://micrometer.io/docs/concepts#_counters)
- Internal Confluence page: *Data‑Enricher Architecture* (ID: ENR‑ARCH‑001)
