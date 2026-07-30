# Test Plan – TES‑99 Validate NETCONF Handler Integration for Cisco IOS‑XR NCS

## Summary
This test plan validates the end‑to‑end flow of the newly added NETCONF subtree handler for Cisco IOS‑XR NCS devices. The verification runs a staged batch (BATCH‑PRB‑20240523‑USE1‑01) in the **staging** environment, inspects SQS traffic, and confirms persisted DTOs in Cassandra.

## Test Cases
1. **Deploy Probe Image** – Deploy the updated `device-probe` Docker image (version `2.7.7‑netconf‑v1`) to the staging Kubernetes namespace.
2. **Trigger Batch** – Use the Fleet‑Orchestrator REST endpoint `/api/v1/orchestrate/batch/BATCH-PRB-20240523-USE1-01` to start the batch.
3. **SQS Inspection – probe.commands** – Verify that a `ProbeJobMessage` with `protocol=NETCONF` and `deviceFamily=IOS‑XR_NCS` is published for each device in the batch.
4. **SQS Inspection – normalize.ingest** – Confirm that a corresponding normalized message appears after the handler processes the NETCONF response.
5. **Cassandra Validation** – Query the `probe.results` table and assert that an `InterfaceRecord` DTO exists for every device with consistency `LOCAL_QUORUM`.
6. **Metrics Check** – Ensure Micrometer metric `probe.protocol.failures{protocol="netconf",deviceFamily="iosxr_ncs"}` reports **0**.
7. **DLQ Verification** – Scan the `probe.commands` DLQ; it must contain **no** messages for this batch.

## Staging Setup
- **Queues**: `probe.commands`, `normalize.ingest`, `platform.results.dlq`
- **Cassandra Table**: `probe.results` (primary key `device_id`)
- **Actuator Endpoint**: `http://staging-probe.internal:8080/actuator/metrics/probe.protocol.failures`
- **AWS Region**: `us-east-1`
- **Consul Service**: `device-probe-staging`

## Pass Criteria
- All devices in the batch return HTTP 200 from the orchestrator trigger.
- Every device yields an `InterfaceRecord` row in `probe.results` with `consistency=LOCAL_QUORUM`.
- Metric `probe.protocol.failures{protocol="netconf",deviceFamily="iosxr_ncs"}` equals **0**.
- DLQ for `probe.commands` remains empty throughout the run.

---
*Prepared by the backend contractor for ticket **PRB‑4821** on 2026‑06‑04.*
