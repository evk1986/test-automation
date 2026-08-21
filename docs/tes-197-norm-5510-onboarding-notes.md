# Onboarding Notes: Schema-Normalizer Pipeline Setup

## Environment Configuration
- **Consul Path**: `config/schema-normalizer/dev`
- **Vault Role**: `schema-normalizer-dev-role`

## Local Docker Setup
To run local SQS and Cassandra dependencies:
```bash
docker-compose up -d localstack cassandra hazelcast
```

## Queue Topology
- **Ingest Queue**: `normalize.ingest`
- **Downstream Output Queue**: `enrich.pipeline`
- **Command Queue**: `probe.commands`
- **Dead Letter Queue**: `platform.results.dlq`

## Architecture Snapshot
Raw protocol dumps published by `Device-Probe` into `normalize.ingest` are picked up by `Norm5510NormalizeIngestHandler`, transformed into canonical DTOs via `Norm5510InterfaceNormalizerService`, and forwarded downstream.