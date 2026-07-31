# ADR‑0051 – Failure‑Rate Metrics Design

## Context
The platform must expose failure‑rate metrics for each device‑probe batch. Operators need alerts when the proportion of failed probe jobs exceeds a threshold.

## Decision
- Use Micrometer `Gauge` named `probe_job_failure_rate` with tags `batchId` and `region`.
- Export to Prometheus via Spring Boot Actuator.
- Define Prometheus alert `failure_rate_high` for > 5 % over 5 minutes.

## Consequences
- Dashboard `Failure Rate` in Grafana will query `probe_job_failure_rate`.
- Alert routing to Ops Slack channel `#network‑ops`.
- Version tag `metricVersion=v2` added to metric description.

# Monitoring Runbook

## Metric Query Examples
```promql
probe_job_failure_rate{batchId="BATCH-PRB-20240523-USE1-01"}
```

## Grafana Dashboard
- URL: https://grafana.internal/netatlas/d/failure-rate

## Escalation Flow
1. Alert fires → PagerDuty → On‑call Engineer.
2. Engineer checks `/actuator/metrics` and recent logs.
3. If failure rate is genuine, open ticket `ENR‑77402`; otherwise, investigate probe connectivity.
