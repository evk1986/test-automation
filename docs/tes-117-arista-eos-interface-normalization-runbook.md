# Runbook – Arista EOS Interface Normalization (ADR‑0043)

## Overview
This runbook documents the normalisation flow for Arista EOS interface data introduced in schema version **v3**. The flow moves from **Schema‑Normalizer** to **Data‑Enricher** via an SNS topic.

## Sequence Diagram
```
Schema‑Normalizer Service
    -> SQS (normalize.ingest)
    -> AristaEosInterfaceNormalizationHandler
    -> AristaEosNormalizationService
    -> SNS (platform.results)
    -> Data‑Enricher Service
```

## Validation Steps
1. **Local Mapper Test**
   - Run `AristaEosNormalizationServiceTest` in IDE.
   - Confirm JSON output matches expected fields and `schemaVersion=v3`.

2. **Staging Validation**
   - Publish a test message to `normalize.ingest` using AWS CLI:
     ```
     aws sqs send-message --queue-url <queue-url> --message-body "Ethernet1,up,1000Mbps"
     ```
   - Verify the normalized record appears in Cassandra table `normalized_record` with `schema_version='v3'`.
   - Check SNS delivery by inspecting the `platform.results` topic subscription logs.

3. **Rollback**
   - If unexpected schema version appears, disable the handler by scaling the deployment to zero replicas and redeploy previous version.

## Monitoring
- **Prometheus metric**: `netatlas_normalizer_processed_total` (labels: `schemaVersion=v3`).
- **Alert**: Spike in `netatlas_normalizer_error_total` > 5/min triggers PagerDuty.

## Contacts
- Owner: **TES‑117** (John Doe, johndoe@example.com)
