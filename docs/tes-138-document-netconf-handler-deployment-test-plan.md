# Test Plan – NETCONF Handler Deployment (TES-138)

## Summary
This test plan validates the end‑to‑end deployment of the new NETCONF subtree handler and the associated Hazelcast lock strategy. It exercises the SQS listener, the deployment service, and the observable metrics.

## Test Cases
1. **SQS Message Consumption** – Send a `ProbeJobMessage` to the `probe.commands` queue and verify that `NetconfSubtreeHandler` invokes `NetconfHandlerDeploymentService.deployHandler` with the correct batch ID.
2. **Hazelcast Lock Acquisition** – Mock `HazelcastInstance` to ensure the lock `netconfHandlerLock` is acquired before deployment logic runs and released afterwards.
3. **Deployment Logic Execution** – Confirm that the service logs the expected deployment steps and does not throw exceptions under normal conditions.
4. **Metric Verification** – After processing, check that the Micrometer counter `netconf.handler.deployments` has incremented and that the lock status metric `hazelcast.lock.netconfHandlerLock` reports `UNLOCKED`.
5. **Rollback via DLQ** – Simulate a failure that pushes the message to `platform.results.dlq` and verify that the rollback checklist (see runbook) can be executed without side effects.

## Staging Setup
- **SQS Queues**: `probe.commands` (standard), `platform.results.dlq` (dead‑letter).
- **Cassandra Table**: `probe_job` (stores job status, not directly used in this slice).
- **Actuator Endpoint**: `http://localhost:8080/actuator/metrics/netconf.handler.deployments`.
- **Hazelcast**: Single‑node cluster for test; lock name `netconfHandlerLock`.

## Pass Criteria
- All test cases execute without errors.
- Deployment counter increments by exactly one per successful message.
- Lock is released after each run.
- Rollback steps complete successfully when a DLQ message is processed.

---
*Prepared for TES‑138 – Document NETCONF handler deployment and Hazelcast lock strategy.*
