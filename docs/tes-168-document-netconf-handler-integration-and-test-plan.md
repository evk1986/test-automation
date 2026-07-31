# Test Plan – TES‑168 – Document NETCONF Handler Integration

## Summary
This test plan validates the newly added NETCONF handler slice (`DocsRunbooksDocsAdrHandler` & `DocsRunbooksDocsAdrService`). The focus is on:
- Correct filtering of SQS messages (only NETCONF jobs are processed).
- Proper interaction with the service layer and metric counters.
- Failure handling that routes the exception to the DLQ.
- End‑to‑end visibility‑timeout reset behavior when a job is retried.

## Test Cases
1. **Happy Path – NETCONF Message**
   - **Input:** `ProbeJobMessage` with protocol `NETCONF`.
   - **Expected:** Service method invoked, `netconf.handler.success` counter incremented, message removed from the queue.
2. **Non‑NETCONF Message Ignored**
   - **Input:** Message with protocol `SNMP`.
   - **Expected:** Service not called, no metric changes, message left untouched (handled by other listeners).
3. **Service Exception Propagation**
   - **Input:** NETCONF message where `DocsRunbooksDocsAdrService.processNetconfJob` throws.
   - **Expected:** `netconf.handler.failure` counter incremented, exception re‑thrown, message moved to DLQ after max retries.
4. **Visibility‑Timeout Reset**
   - **Pre‑condition:** Message is received, processing takes longer than the default visibility timeout.
   - **Action:** Handler calls `service.processNetconfJob` which internally updates the job status; the test simulates a manual call to `ChangeMessageVisibility` via the AWS SDK mock.
   - **Expected:** Visibility timeout is extended, the message remains invisible to other consumers until processing completes.
5. **Metric Verification in Staging**
   - **Action:** Deploy the handler to the `staging` environment and trigger a batch of NETCONF jobs.
   - **Expected:** Prometheus shows increasing `netconf_handler_success_total` and `netconf_handler_failure_total` counters matching the number of processed jobs.

## Staging Setup
- **SQS Queue:** `probe.commands` (standard queue) – DLQ configured as `platform.results.dlq`.
- **Cassandra Table:** `probe_job` (primary key `job_id`). Ensure the table contains a row for `JOB-NETCONF-4821` before test execution.
- **SNS Topic:** `platform.results` – subscribed by Schema‑Normalizer.
- **Actuator Endpoint:** `http://staging-probe.internal:8080/actuator/metrics` – verify metric names `netconf.handler.success` and `netconf.handler.failure`.
- **IAM Role:** The pod runs with a Vault‑derived AWS IAM role that permits `sqs:ReceiveMessage`, `sqs:DeleteMessage`, `sqs:ChangeMessageVisibility`, and `sns:Publish`.

## Pass Criteria
- All test cases execute without errors.
- Success counter increments exactly once per successful NETCONF message.
- Failure counter increments exactly once per exception.
- After a simulated long‑running job, the message visibility timeout is successfully extended and the job completes without being redelivered.
- Prometheus metrics reflect the observed counts within a 30‑second scrape window.

---
*Prepared by the Device‑Probe team for ticket **PRB‑4821** on 2026‑06‑09.*