# Onboarding – Arista EOS eAPI Mapper (TES-160)

## Overview
The mapper lives in the `com.internal.netatlas.normalize` module. It consumes messages from the SQS queue `normalize.ingest`, transforms them, and publishes to the SNS topic `enrich.pipeline`.

## Repository Layout
```
src/main/java/com/internal/netatlas/normalize/
│   ├─ handler/   ← SQS listener (`AristaEosInterfaceMapperHandler`)
│   └─ service/   ← Core mapping logic (`AristaEosInterfaceMapperService`)
src/test/java/com/internal/netatlas/normalize/service/AristaEosInterfaceMapperServiceTest.java
```

## Consul Paths
- `config/schema-normalizer/mapper/arista-eos` – holds feature flags (e.g., `enabled=true`).
- `service/registry/schema-normalizer` – registers the service instance for discovery.

## Vault Role
- Role name: `schema-normalizer`.
- Secrets accessed: `aws/credentials` (for SNS) and `db/cassandra` (not used by the mapper but required for service start‑up).

## Docker‑Compose (local dev)
```yaml
services:
  schema-normalizer:
    image: netatlas/schema-normalizer:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - AWS_REGION=us-east-1
    ports:
      - "8080:8080"
    depends_on:
      - localstack
  localstack:
    image: localstack/localstack:latest
    environment:
      - SERVICES=sqs,sns
    ports:
      - "4566:4566"
```

## Queue Topology
- **Producer**: `Device-Probe` publishes raw EOS JSON to `normalize.ingest`.
- **Consumer**: `AristaEosInterfaceMapperHandler` (this service).
- **Downstream**: `Data‑Enricher` subscribes to `enrich.pipeline`.

## Getting Started
1. Run `docker-compose up -d`.
2. Create the SQS queue and SNS topic in LocalStack (scripts provided in `infra/`).
3. Execute `./mvnw clean test` – all unit tests should pass.
4. Deploy to `dev` environment using the standard CI pipeline.
