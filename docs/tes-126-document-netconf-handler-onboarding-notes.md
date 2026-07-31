# Onboarding Notes – NETCONF Handler (TES‑126)

## Consul Service Registration
- Service name: `probe-service`
- Health check URL: `http://localhost:8080/actuator/health`
- Consul KV path for configuration: `netatlas/probe/config/netconf`

## Vault Role
- Role name: `netatlas-probe`
- Policies: `netatlas/probe/read`, `netatlas/probe/write`
- Secrets accessed: `secret/data/netatlas/probe/netconf/credentials`

## Docker‑Compose Development Setup
```yaml
version: "3.8"
services:
  probe:
    image: registry.internal/netatlas-probe:netconf‑v2.0.0
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - VAULT_ROLE=netatlas-probe
    ports:
      - "8080:8080"
    depends_on:
      - localstack
  localstack:
    image: localstack/localstack
    environment:
      - SERVICES=sqs,sns
    ports:
      - "4566:4566"
```
- After `docker-compose up`, the `probe.commands` queue is available at `http://localhost:4566`.

## Queue Topology
- **Inbound**: `probe.commands` (standard SQS) – receives `NetconfJobMessage`.
- **DLQ**: `platform.results.dlq` – captures failed NETCONF jobs.
- **Outbound**: SNS topic `device-probe-results` – downstream consumers (Schema‑Normalizer).

## Additional Resources
- NETCONF adapter implementation: `com.internal.netatlas.probe.protocol.NetconfAdapter`.
- Logging conventions: use `Slf4j` with correlation ID `jobId`.
- Monitoring dashboards: Prometheus query `probe_netconf_processing_seconds_count`.
