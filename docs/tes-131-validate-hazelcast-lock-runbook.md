# Runbook – TES-131 – Hazelcast Lock Validation for SNMP Walks

## Purpose
Provide operators with step‑by‑step instructions to verify that the Hazelcast lock correctly serialises SNMP walk jobs in the **Device‑Probe** service.

## Prerequisites
- Access to the `staging` Kubernetes namespace (`netatlas-staging`).
- `kubectl` configured for the target cluster.
- AWS credentials with permission to read the `probe.commands` SQS queue.
- Access to the Cassandra keyspace `netatlas`.

## Procedure
1. **Deploy Updated Image**
   ```bash
   kubectl set image deployment/device-probe device-probe=repo.internal/netatlas/device-probe:release-2024-05-23-PRB-4821
   ```
   Verify rollout status:
   ```bash
   kubectl rollout status deployment/device-probe -n netatlas-staging
   ```

2. **Trigger the Batch**
   Use the orchestrator REST endpoint to start the batch:
   ```bash
   curl -X POST \
        -H "Content-Type: application/json" \
        https://staging-orchestrator.internal/api/v1/batches/start \
        -d '{"batchId":"BATCH-PRB-20240523-USE1-01","deviceFamily":"SNMP","region":"us-east-1"}'
   ```

3. **Monitor Logs**
   Stream logs from the probe pods:
   ```bash
   kubectl logs -l app=device-probe -c probe -f -n netatlas-staging | grep "Hazelcast lock"
   ```
   Look for the acquisition and release messages for each device.

4. **Validate Cassandra Results**
   Connect to Cassandra and run:
   ```cql
   SELECT device_id, count(*) AS walk_count
   FROM netatlas.probe_results
   WHERE batch_id='BATCH-PRB-20240523-USE1-01'
   GROUP BY device_id;
   ```
   Ensure `walk_count` equals **1** for every device.

5. **Check Hazelcast Lock State**
   Access the Hazelcast metrics endpoint (exposed via Actuator):
   ```bash
   curl http://staging-device-probe.internal:8080/actuator/metrics/hazelcast.locks
   ```
   Confirm that the metric `hazelcast.locks` reports **0** for the `snmp-walk-*` keys.

6. **Cleanup**
   If any lock entries remain, restart the affected pod:
   ```bash
   kubectl rollout restart deployment/device-probe -n netatlas-staging
   ```

## Escalation
- If duplicate rows are found in Cassandra, open a ticket **PRB-4821** and attach the log excerpts.
- If locks are not released, verify the Hazelcast client configuration in `com.internal.netatlas.probe.config.HazelcastConfig`.

## Documentation Links
- [Hazelcast Locking Guide](https://docs.hazelcast.com/hazelcast/latest/distributed-locks)
- [Device‑Probe Architecture Overview](../architecture/device-probe.md)
