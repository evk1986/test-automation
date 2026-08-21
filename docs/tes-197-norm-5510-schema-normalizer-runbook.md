# Runbook: NORM-5510 Schema-Normalizer v3 Pipeline Updates

## Operational Overview
The `Schema-Normalizer` service consumes raw probe snapshots from `normalize.ingest`, maps vendor-specific structures (including Arista EOS eAPI payloads) into canonical schema `v3`, and publishes outputs to downstream enrichment queues.

## Error Handling & Dead Letter Queue (DLQ)
- Transient parsing failures or unhandled malformed payloads trigger automated retries.
- Messages exceeding execution retry limits are routed directly to `platform.results.dlq`.
- Monitor queue backlog using AWS SQS CloudWatch metrics `ApproximateNumberOfMessagesVisible` for `normalize.ingest`.

## Monitoring & Verification
- Access service health at `http://<node-ip>:8080/actuator/health`.
- Prometheus metrics available at `/actuator/prometheus` under metric prefix `normalizer_records_processed_total`.

## Rollback Strategy
In the event of unexpected schema degradation:
1. Revert deployment image to previous production tag.
2. Ingest queue `normalize.ingest` will continue buffering messages without loss during rollback deployment.