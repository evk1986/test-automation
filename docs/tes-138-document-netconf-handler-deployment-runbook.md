# Runbook – Deploy NETCONF Subtree Handler & Hazelcast Lock (TES‑138)

## Overview
This runbook describes how to roll out the new NETCONF subtree handler (`NetconfSubtreeHandler`) to the **Device‑Probe** service, configure the Hazelcast distributed lock, and validate that the deployment succeeded via Micrometer metrics.

## Prerequisites
- Access to the `prod-use1` Kubernetes namespace.
- Vault role `netatlas-probe` with permission to read `secret/probe/netconf`.
- Consul service `probe.handlers.netconf` registered.
- AWS credentials with permission to read/write the SQS queues `probe.commands` and `platform.results.dlq`.

## Deployment Checklist
1. **Build & Publish Docker Image**
   ```bash
   ./gradlew clean bootJar
   docker build -t 123456789012.dkr.ecr.us-east-1.amazonaws.com/netatlas-probe:tes-138 .
   aws ecr push 123456789012.dkr.ecr.us-east-1.amazonaws.com/netatlas-probe:tes-138
   ```
2. **Update Helm Values** (`values.yaml`)
   ```yaml
   image:
     tag: tes-138
   env:
     - name: HAZELCAST_LOCK_NAME
       value: netconfHandlerLock
   ```
3. **Apply Helm Release**
   ```bash
   helm upgrade --install probe-service ./helm/probe -n prod-use1 -f values.yaml
   ```
4. **Verify Pod Status**
   ```bash
   kubectl get pods -n prod-use1 -l app=probe-service
   ```
5. **Confirm Hazelcast Lock Registration**
   ```bash
   curl http://probe-service.prod-use1.svc.cluster.local:8080/actuator/metrics/hazelcast.lock.netconfHandlerLock
   ```
   Expected output contains `value=0` (unlocked).

## Rollback Procedure (DLQ)
1. **Pause Consumption**
   ```bash
   aws sqs set-queue-attributes --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands --attributes VisibilityTimeout=0
   ```
2. **Inspect DLQ**
   ```bash
   aws sqs receive-message --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/platform.results.dlq --max-number-of-messages 10
   ```
3. **Redeploy Previous Image**
   ```bash
   helm upgrade --install probe-service ./helm/probe -n prod-use1 -f values.yaml --set image.tag=release-2024-05-20
   ```
4. **Clear DLQ** (after confirming no pending work)
   ```bash
   aws sqs purge-queue --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/platform.results.dlq
   ```

## Metrics Verification
- **Deployment Counter**
  ```bash
  curl http://probe-service.prod-use1.svc.cluster.local:8080/actuator/metrics/netconf.handler.deployments
  ```
  Verify the `count` increased by the number of processed jobs.
- **Lock Status**
  ```bash
  curl http://probe-service.prod-use1.svc.cluster.local:8080/actuator/metrics/hazelcast.lock.netconfHandlerLock
  ```
  The metric should show `value=0` when idle and `value=1` only during active deployment.

## Troubleshooting Tips
- **Lock Stuck** – If `value=1` persists, manually unlock:
  ```bash
  curl -X POST http://probe-service.prod-use1.svc.cluster.local:8080/actuator/hazelcast/locks/netconfHandlerLock/unlock
  ```
- **Missing Metrics** – Ensure `micrometer-registry-prometheus` is on the classpath and `management.endpoints.web.exposure.include=*` is set.
- **SQS Permission Errors** – Verify IAM role attached to the pod has `sqs:SendMessage`, `sqs:ReceiveMessage`, and `sqs:DeleteMessage` on both queues.

---
*Runbook approved by Platform Architecture Review (2026‑06‑09).*
