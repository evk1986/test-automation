# Onboarding Notes – TES‑125 – NETCONF Handler Integration

## Overview
These notes help a new developer get the staging environment ready to work on the NETCONF handler slice introduced in ticket **TES‑125**.

## Consul Service Discovery
- **Path**: `netatlas/services/probe`
- Keys:
  - `sqs.queue.probe.commands = probe.commands`
  - `cassandra.keyspace = netatlas`
  - `hazelcast.cluster.name = netatlas-hz`
- Register the service with the Consul agent using the Spring Cloud Consul starter – the `application.yml` already contains:
  ```yaml
  spring:
    cloud:
      consul:
        discovery:
          service-name: probe
          health-check-path: /actuator/health
  ```

## Vault Role & Secrets
- **Vault role**: `netatlas-probe-staging`
- Secrets needed:
  - `aws/creds/probe-sqs` – SQS access keys.
  - `cassandra/creds/probe` – Cassandra username/password.
- The Spring Boot Vault integration pulls these at startup; ensure the `VAULT_TOKEN` environment variable is set for the local container.

## Docker‑Compose Development Stack
```yaml
version: "3.8"
services:
  probe:
    image: netatlas/probe:latest
    environment:
      - SPRING_PROFILES_ACTIVE=staging
      - VAULT_TOKEN=${VAULT_TOKEN}
    ports:
      - "8080:8080"
    depends_on:
      - local-sqs
      - local-cassandra
  local-sqs:
    image: softwaremill/elasticmq
    ports:
      - "9324:9324"
  local-cassandra:
    image: cassandra:4.0
    ports:
      - "9042:9042"
```
- Bring the stack up with `docker-compose up -d`.
- Verify the `probe.commands` queue exists via the ElasticMQ UI (`http://localhost:9324`).

## Queue Topology
- **Inbound**: `probe.commands` (SQS) – messages produced by Fleet‑Orchestrator.
- **Outbound**: `normalize.ingest` (SNS) – not used in this slice but present for downstream services.
- **DLQ**: `platform.results.dlq` – monitored by the `platform.dlq` metric.

## Build & Run
```bash
./mvnw clean verify          # runs unit tests including NetconfBatchHandlerTest
./mvnw spring-boot:run -Dspring-boot.run.profiles=staging
```
- The application will register with Consul, connect to the local SQS and Cassandra, and expose Actuator endpoints.

## Helpful Commands
- **Check Hazelcast lock status**:
  ```bash
  curl http://localhost:8080/actuator/hazelcast | jq '.locks[] | select(.name | contains("netconf-batch"))'
  ```
- **View Micrometer metrics**:
  ```bash
  curl http://localhost:8080/actuator/metrics | jq '.names[]'
  ```

---
*Compiled for onboarding on ticket **TES‑125** – 2026‑06‑05.*