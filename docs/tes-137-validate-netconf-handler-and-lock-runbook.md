# Runbook – TES‑137 – NETCONF Handler & Lock Validation

## Purpose
Provide operators with step‑by‑step instructions to execute the integration test suite for the NETCONF handler (Cisco IOS‑XR NCS) and to verify correct lock behavior in the **staging** environment.

## Prerequisites
| Item | Requirement |
|------|--------------|
| Access | VPN to `staging` VPC and IAM permission `sqs:ReceiveMessage`, `sqs:SendMessage`, `cassandra:Read`, `cassandra:Write` |
| Vault Role | `staging/netatlas/probe` – contains DB credentials and Hazelcast TLS keystore |
| Monitoring | Prometheus UI reachable at `http://prometheus.staging.internal.netatlas.com` |
| Tools | `awscli`, `cqlsh`, `hazelcast-cli`, `jq`, `curl` |

## Execution Steps
1. **Prepare Batch Identifier**
   ```bash
   BATCH_ID="BATCH-PRB-20240523-USE1-01"
   ```
2. **Verify Queue Depth**
   ```bash
   aws sqs get-queue-attributes --queue-url $(aws sqs get-queue-url --queue-name probe.commands --output text) \
       --attribute-names ApproximateNumberOfMessages
   ```
   Ensure the count matches the expected number of devices for the batch.
3. **Trigger Orchestrator**
   ```bash
   curl -X POST "http://staging-orchestrator.internal.netatlas.com/api/v1/batches/${BATCH_ID}/start" \
        -H "Authorization: Bearer $(vault read -field=token secret/staging/netatlas/orchestrator)"
   ```
4. **Monitor Handler Logs**
   ```bash
   kubectl logs -f deployment/probe-service -n staging --container=app | grep NETCONF
   ```
   Look for `Lock acquired` / `Lock released` messages.
5. **Replay DLQ (optional)**
   ```bash
   aws sqs receive-message --queue-url $(aws sqs get-queue-url --queue-name platform.results.dlq --output text) \
       --max-number-of-messages 10 --output json | jq -c '.Messages[]' | while read msg; do
       aws sqs send-message --queue-url $(aws sqs get-queue-url --queue-name probe.commands --output text) \
           --message-body "$(echo $msg | jq -r .Body)"
   done
   ```
6. **Validate Metrics**
   ```bash
   curl -s http://staging-probe.internal.netatlas.com/actuator/prometheus | grep "probe_netconf_"
   ```
   Expected keys:
   - `probe_netconf_success`
   - `probe_netconf_failure`
   - `probe_netconf_lock_acquired`
   - `probe_netconf_lock_released`
7. **Check for Duplicate Sessions**
   ```bash
   kubectl logs deployment/probe-service -n staging | grep "Duplicate NETCONF session"
   ```
   The command should return no lines.
8. **Cleanup**
   - Ensure all locks are released (Hazelcast CLI: `hazelcast-cli lock list`).
   - Purge any test messages left in `probe.commands`.

## Rollback
If dead‑locks are observed:
1. Stop the `probe-service` deployment: `kubectl scale deployment/probe-service --replicas=0 -n staging`.
2. Release all Hazelcast locks manually: `hazelcast-cli lock force-unlock netconf-lock-*`.
3. Restart the service: `kubectl scale deployment/probe-service --replicas=3 -n staging`.
4. Re‑run the batch.

## Owner
- Primary: **Alice Nguyen** (Platform Engineer – NetAtlas Probe)
- Backup: **Bob Patel** (Observability Engineer)
