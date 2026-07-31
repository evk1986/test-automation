# Onboarding Notes – TES‑135 Failure‑Rate Metrics

## Consul Paths
- `services/orchestrate/monitoring` – health‑check endpoint.
- `config/metrics/version` – current metric version (`v2`).

## Vault Role
- Role `netatlas-orchestrate` grants read access to `secret/data/metrics`.

## Docker‑Compose
```yaml
services:
  orchestrate:
    image: netatlas/orchestrate:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - VAULT_ROLE=netatlas-orchestrate
    ports:
      - "8080:8080"
```

## Queue Topology
- No SQS queues are used by this feature; only HTTP calls.

## Helpful Commands
```bash
curl http://localhost:8080/api/v1/monitoring/runbook
curl http://localhost:8080/actuator/metrics/probe_job_failure_rate
```
