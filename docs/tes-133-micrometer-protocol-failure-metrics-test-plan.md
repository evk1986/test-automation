# TES-133 – Micrometer Protocol Failure Metrics Test Plan

## Summary
This test plan validates that failure counters for each Device‑Probe protocol are
* created and exposed via Spring Boot Actuator at **/actuator/metrics/probe.protocol.failures**
* increment correctly when the `ProtocolFailureMetricsService` records a failure.
The verification is performed on the **dev** environment (region us‑east‑1) using a
Prometheus scrape job.

## Test Cases
1. **Counter Presence** – Query the Actuator endpoint and assert that a metric
   named `probe.protocol.failures` exists with the tag `protocol` for the values
   `netconf`, `ssh`, `snmp`, `eapi`, and `grpc`.
2. **Increment on Failure** – Invoke `ProtocolFailureMetricsService.recordFailure`
   for each protocol and confirm that the corresponding counter value increases
   by **1**.
3. **Prometheus Scrape** – Ensure the dev Prometheus server successfully scrapes
   the `/actuator/metrics/probe.protocol.failures` endpoint and stores the metric
   with the correct tags.
4. **Idempotent Recording** – Call the service twice for the same protocol and
   verify the counter reflects **2** increments.

## Staging Setup
* **Queue names** – No queue interaction is required for this metric test.
* **Cassandra tables** – Not applicable.
* **Actuator endpoint** – `http://dev-probe.internal.netatlas.com:8080/actuator/metrics/probe.protocol.failures`
* **Prometheus job** –
  ```yaml
  - job_name: 'device-probe-dev'
    metrics_path: '/actuator/metrics/probe.protocol.failures'
    static_configs:
      - targets: ['dev-probe.internal.netatlas.com:8080']
  ```

## Pass Criteria
* All five protocol tags are present in the Actuator response.
* Counter values match the number of recorded failures for each protocol.
* Prometheus shows the metric with the correct tags and non‑zero values after the test run.
* No errors are logged by the `ProtocolFailureMetricsService` during execution.

---
*Ticket:* TES-133 | *Date:* 2026‑06‑28 | *Author:* Junior Backend Contractor