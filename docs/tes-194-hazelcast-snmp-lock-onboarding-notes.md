# Onboarding Exploration Notes: Device-Probe Lock & Idempotency System

## Scope
Notes recorded while investigating Hazelcast distributed locking and SQS idempotency mechanisms for PRB-4821.

## Architecture Overview
- **Consul Path**: `config/device-probe/staging`
- **Vault Secret Role**: `device-probe-role`
- **Queue Topology**: `probe.commands` (SQS) -> `Device-Probe` -> `normalize.ingest` (SQS)

## Local Testing Setup
Run local stack via docker-compose:
```bash
docker-compose up -d hazelcast cassandra localstack
```

Verify metrics endpoint locally:
```bash
curl http://localhost:8080/actuator/metrics/probe.snmp.idempotent
```