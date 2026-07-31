# Runbook – Data‑Enricher SQS Consumer Enhancements (ENR‑77402)

## Overview
This runbook documents the deployment, verification, and rollback steps for the enhanced Data‑Enricher consumer that now supports idempotent writes and dynamic visibility‑timeout extension.

## Prerequisites
* Access to the `prod-use1` and `staging` AWS accounts.
* Vault role `netatlas/enricher` with permission to read the `aws/iam` credentials.
* Consul service `netatlas-enricher` registered under `services/netatlas/enricher`.
* Micrometer Prometheus endpoint reachable from the monitoring cluster.

## Deployment Steps
1. **Build & Publish**
   ```bash
   ./mvnw clean package -DskipTests
   docker build -t 123456789012.dkr.ecr.us-east-1.amazonaws.com/netatlas-enricher:tes-140 .
   aws ecr push 123456789012.dkr.ecr.us-east-1.amazonaws.com/netatlas-enricher:tes-140
   ```
2. **Update Kubernetes Deployment**
   ```yaml
   spec:
     containers:
       - name: enricher
         image: 123456789012.dkr.ecr.us-east-1.amazonaws.com/netatlas-enricher:tes-140
         envFrom:
           - secretRef:
               name: netatlas-enricher-vault
   ```
   Apply with `kubectl apply -f deployment.yaml`.
3. **Verify Service Registration**
   ```bash
   consul catalog services | grep netatlas-enricher
   ```
4. **Smoke Test** – Send a single test message to `enrich.pipeline` and confirm a row appears in Cassandra.
5. **Run Full QA Suite** – Execute the test plan located at `docs/tes-140-qa-for-data-enricher-sqs-consumer-enhancements-test-plan.md`.

## Rollback Procedure
If any step fails or metrics indicate duplicate writes:
1. Scale the new replica set to `0`:
   ```bash
   kubectl scale deployment netatlas-enricher --replicas=0
   ```
2. Re‑apply the previous image tag (e.g., `release-1.12.3`).
3. Verify that the old consumer processes messages without duplication.
4. Notify the incident response team.

## Monitoring & Alerts
* **Metric**: `enricher.results.written` – should increase monotonically.
* **Alert**: `EnricherDuplicateWrites` – fires if the Cassandra query `SELECT count(*) FROM enrichment_result WHERE normalized_record_id = ?` returns >1 for the same ID.
* **DLQ Lag**: Watch `platform.results.dlq` depth; alert on >0 messages after a batch run.

---
*Runbook authored by the Data‑Enricher engineering team, version 1.0 (2026‑06‑16).*