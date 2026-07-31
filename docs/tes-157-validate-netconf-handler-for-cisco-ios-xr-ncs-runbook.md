# Runbook – Deploy & Validate NETCONF Handler (TES‑157)

## Purpose
Provide step‑by‑step instructions for developers and QA engineers to deploy the **TestIntegrationNetconf** handler into the `dev` environment and verify its correct operation against a Cisco IOS‑XR NCS batch.

## Prerequisites
- Access to the internal Docker registry and Kubernetes cluster for the `dev` namespace.
- AWS credentials with permissions to read/write the `probe.commands` and `normalize.ingest` queues, and to publish to the SNS topic.
- Vault role `netatlas/dev/probe` configured for the service account.
- Consul service `device-probe-dev` registered.

## Deployment Steps
1. **Build Docker Image**
   ```bash
   ./gradlew clean bootJar
   docker build -t internal.registry.netatlas/device-probe:dev-$(git rev-parse --short HEAD) .
   docker push internal.registry.netatlas/device-probe:dev-$(git rev-parse --short HEAD)
   ```
2. **Update Helm values** (or kustomize overlay) to reference the new image tag.
3. **Apply Kubernetes manifests**
   ```bash
   helm upgrade --install device-probe-dev ./helm/device-probe \
       --namespace dev \
       --set image.tag=dev-$(git rev-parse --short HEAD)
   ```
4. **Verify health**
   ```bash
   curl -s http://device-probe-dev.dev.svc.cluster.local:8080/actuator/health | jq .status
   # Expected output: "UP"
   ```

## Trigger Batch Processing
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"batchId":"BATCH-PRB-20240523-USE1-01"}' \
  http://orchestrator-dev.dev.svc.cluster.local:8080/api/v1/orchestrator/batches
```
The orchestrator will enqueue a series of `ProbeJobMessage` records onto the `probe.commands` queue.

## Validation Checklist
| Item | Command / Query | Expected Outcome |
|------|-----------------|------------------|
| Queue depth | `aws sqs get-queue-attributes --queue-url <probe.commands-url> --attribute-names ApproximateNumberOfMessages` | Depth matches number of NCS devices in the batch. |
| Snapshot rows | `cqlsh -e "SELECT count(*) FROM netatlas_dev.device_snapshot WHERE job_id='BATCH-PRB-20240523-USE1-01';"` | Count equals device count. |
| SNS fan‑out | Pull messages from `test-normalize-ingest` queue. | One message per snapshot ID. |
| Canonical DTOs | `cqlsh -e "SELECT * FROM netatlas_dev.device_results WHERE canonical_type='InterfaceRecord' AND batch_id='BATCH-PRB-20240523-USE1-01';"` | All rows present, no duplicate `id`. |
| Metrics | `curl http://device-probe-dev:8080/actuator/metrics | grep probe_protocol_failures` | Counter unchanged from baseline. |

## Rollback Procedure
1. Scale the Deployment to `0` replicas:
   ```bash
   kubectl scale deployment/device-probe-dev --replicas=0 -n dev
   ```
2. Delete the batch from the orchestrator (if still running):
   ```bash
   curl -X DELETE http://orchestrator-dev:8080/api/v1/orchestrator/batches/BATCH-PRB-20240523-USE1-01
   ```
3. Purge any test messages from `probe.commands` and `normalize.ingest` queues using the AWS CLI.

## Contact
- **Owner**: Jane Doe (jdoe@netatlas.internal)
- **Pager**: `+1-555-123-4567` (on‑call for Device‑Probe)
