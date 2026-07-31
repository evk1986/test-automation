# Runbook – Hazelcast Lock & SNMP Failure Metric Validation (TES-143)

## Purpose
Provide operators with a repeatable procedure to verify that the Device‑Probe service correctly serializes SNMP walks per device and that failure metrics are reported.

## Prerequisites
- Access to the **dev** or **staging** environment where the `probe.commands` queue is reachable.
- `kubectl` configured for the target namespace.
- `aws` CLI with permissions to read/write the SQS queue.
- `curl` for metric scraping.

## Steps
1. **Deploy Test Harness**
   ```bash
   kubectl apply -f k8s/test-harness-probe.yaml -n dev
   ```
   The manifest runs the `IntegrationTestSuiteSqsReplayHandlerTest` as a one‑off job.
2. **Inject Test Messages**
   ```bash
   aws sqs send-message-batch --queue-url $PROBE_COMMANDS_URL \
       --entries file://test-messages.json
   ```
   `test-messages.json` contains 20 messages, 3 with `"injectFailure":true`.
3. **Monitor Logs**
   ```bash
   kubectl logs -f job/integration-test-probe -n dev
   ```
   Look for `Acquired lock` / `Released lock` pairs.
4. **Scrape Metrics**
   ```bash
   curl -s http://probe-service.dev.svc.cluster.local:8080/actuator/metrics/probe.protocol.failures | jq .
   ```
   Verify the `measurements[0].value` equals 3.
5. **Cleanup**
   ```bash
   kubectl delete -f k8s/test-harness-probe.yaml -n dev
   ```

## Troubleshooting
- **No lock logs** – Ensure the test job uses the same Hazelcast cluster as the service (check `hazelcast.cluster-name`).
- **Metric missing** – Confirm `MeterRegistry` bean is loaded in the test profile and that the counter name matches exactly `probe.protocol.failures`.
- **Duplicate walks observed** – Verify that the Hazelcast version is consistent across pods; mismatched versions can break distributed lock semantics.

---
*Runbook authored for ticket TES-143 (PRB-874).*