# Onboarding – Metrics Validation Workstream (TES‑134)

## Consul paths
- `config/device-probe/metrics` – Micrometer enable flag and tag defaults.
- `services/device-probe` – Service registration details.

## Vault role
- Role name: `device-probe-dev`
- Policies grant read access to `secret/dev/device-probe/*` for Prometheus credentials.

## Docker‑compose setup (local dev)
```yaml
version: "3.8"
services:
  device-probe:
    image: internal/device-probe:dev
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - VAULT_ROLE=device-probe-dev
    ports:
      - "8080:8080"
  prometheus:
    image: prom/prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
```

## Queue topology
- **Input queue**: `probe.commands` (SQS) – receives probe jobs.
- **DLQ**: `platform.results.dlq` – captures unrecoverable failures.
- **Metrics exposure**: `/actuator/prometheus` on the Device‑Probe pod.

## Helpful links
- Micrometer docs: https://micrometer.io/docs
- Prometheus alert rule definition: `alerts/device-probe.yaml`
- Grafana dashboard JSON: `dashboards/device-probe-protocol-failures.json`
