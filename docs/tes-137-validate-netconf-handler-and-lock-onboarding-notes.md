# Onboarding Notes – TES‑137 – NETCONF Handler & Lock Integration

## Overview
These notes help a new team member understand the components involved in the NETCONF handler validation and how to spin up a local development environment that mirrors the staging pipeline.

## Consul Service Paths
| Service | Consul KV Path |
|---------|----------------|
| Probe Service | `netatlas/probe/config` |
| Hazelcast Config | `netatlas/hazelcast/config` |
| Vault Role Mapping | `netatlas/vault/roles/staging/probe` |

## Vault Secrets
- **Path:** `secret/staging/netatlas/probe`
- **Keys:** `cassandra_username`, `cassandra_password`, `hazelcast_tls_keystore`, `hazelcast_tls_password`
- Retrieve with: `vault kv get -format=json secret/staging/netatlas/probe`

## Docker‑Compose Quick‑Start
```yaml
version: "3.8"
services:
  probe-service:
    image: internal/netatlas-probe:latest
    environment:
      - SPRING_PROFILES_ACTIVE=staging
      - AWS_REGION=us-east-1
      - AWS_SQS_PROBE_COMMANDS=probe.commands
    ports:
      - "8080:8080"
    depends_on:
      - hazelcast
      - cassandra
  hazelcast:
    image: hazelcast/hazelcast:5.3
    ports:
      - "5701:5701"
  cassandra:
    image: cassandra:4.0
    ports:
      - "9042:9042"
    environment:
      - CASSANDRA_CLUSTER_NAME=netatlas
```
Run with `docker-compose up -d`. The service will automatically register with the local Consul agent if you have Consul running (`consul agent -dev`).

## Queue Topology
- **Inbound:** `probe.commands` (SQS) – receives `ProbeJobMessage` objects.
- **Dead‑Letter:** `platform.results.dlq` – holds failed messages for replay.
- **Outbound (SNS):** Not used in this test slice but configured as `netatlas.probe.results.topic`.

## Key Classes to Review
- `ProbeTestsNetconfHandler` – SQS listener that delegates to the service.
- `ProbeTestsNetconfService` – Handles Hazelcast lock, invokes `NetconfAdapter`, records Micrometer metrics.
- `NetconfAdapter` (interface) – Implemented by `CiscoIosXrNetconfAdapter` (vendor‑specific).

## Debugging Tips
- Enable TRACE logging for `com.internal.netatlas.probe` via `application-staging.yml`:
  ```yaml
  logging:
    level:
      com.internal.netatlas.probe: TRACE
  ```
- Use `hazelcast-cli lock list` to view currently held locks.
- Metrics are exposed on `/actuator/prometheus`; you can curl locally: `curl http://localhost:8080/actuator/prometheus | grep probe_netconf`.

## Next Steps
1. Run the integration test suite (`./gradlew testIntegration`).
2. Verify lock metrics and ensure no dead‑locks appear.
3. Submit a PR with any discovered issues.
