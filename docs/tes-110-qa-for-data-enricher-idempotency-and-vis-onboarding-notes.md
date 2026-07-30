# Onboarding Notes – Data‑Enricher Idempotency Test (TES‑110)

## Overview
This document captures the essential environment details required to run the idempotency and visibility‑timeout QA for the Data‑Enricher service.

## Consul Paths
- `config/enrich/service` – Spring Boot configuration (SQS queue URLs, Cassandra keyspace).
- `service/discovery/enrich` – Service registration for health checks.

## Vault Role
- Role: `netatlas/enrich` with policies allowing read access to `secret/data/netatlas/enrich/*`.
- AWS IAM auth method is used; token TTL is 1 hour.

## Docker‑Compose (local dev)
```yaml
version: "3.8"
services:
  enrich-app:
    image: netatlas/enrich:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - VAULT_ROLE=netatlas/enrich
    ports:
      - "8080:8080"
    depends_on:
      - cassandra
      - localstack
  cassandra:
    image: cassandra:4.0
    ports:
      - "9042:9042"
  localstack:
    image: localstack/localstack:latest
    environment:
      - SERVICES=sqs,sns
    ports:
      - "4566:4566"
```

## Queue Topology
- **Input Queue**: `enrich.pipeline` (standard SQS).
- **Dead‑Letter Queue**: `platform.results.dlq` – receives messages after 5 failed attempts.
- **Fan‑out SNS**: `enrich.results.topic` – downstream consumers subscribe.

## Useful Commands
- List queues: `aws sqs list-queues --endpoint-url http://localhost:4566`
- View metrics: `curl http://localhost:8080/actuator/metrics/sqs.visibilityTimeout`
- Check Cassandra rows: `cqlsh> SELECT * FROM enrich.results LIMIT 10;`

---
*Prepared by: junior‑backend‑contractor – 2026‑07‑30*
