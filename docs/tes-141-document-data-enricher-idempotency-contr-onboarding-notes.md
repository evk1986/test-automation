# Onboarding – TES‑141 – Data‑Enricher Idempotency Feature

## Overview
This feature adds a public endpoint that reports the number of idempotency keys stored in Cassandra and documents the dynamic SQS visibility‑timeout mechanism.

## Consul Paths
- `service/data-enricher/config` – contains `visibilityTimeoutSeconds` (default 60).
- `service/data-enricher/health` – health check endpoint.

## Vault Role
- Role name: `data-enricher-app`
- Policies grant read access to `secret/data-enricher/*` for DB credentials.

## Local Development
```bash
docker compose -f docker-compose.dev.yml up -d
# Wait for Cassandra to be ready
cqlsh -e "CREATE KEYSPACE IF NOT EXISTS enrich WITH replication = {'class':'SimpleStrategy','replication_factor':1};"
cqlsh -e "CREATE TABLE IF NOT EXISTS enrich.idempotency_key (key text PRIMARY KEY, created_at timestamp);"
```
Run the Spring Boot app:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```
The endpoint is reachable at `http://localhost:8080/api/v1/enrich/idempotency/info`.

## Queue Topology
- Producer: `DataEnricher` publishes to SNS topic `platform.results`.
- Consumer: `EnrichmentWorker` reads from SQS queue `enrich.pipeline` with the visibility timeout defined in Consul.

## Next Steps
- Review the ADR (`docs/adr-0045.md`) with the architecture team.
- Add integration tests for the new endpoint.
