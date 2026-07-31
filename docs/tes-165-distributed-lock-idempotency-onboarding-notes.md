# Onboarding Notes – Distributed Lock & Idempotency (TES‑165)

## Consul Paths
- `service/probe/config/hazelcast` – Hazelcast client configuration (cluster members, TLS settings).
- `service/probe/config/sqs` – SQS queue names (`probe.commands`, `platform.results.dlq`).
- `service/probe/health` – Actuator health endpoint registration.

## Vault Role
- Role name: `netatlas-probe-role`
- Policies: `hazelcast-read`, `sqs-read-write`, `cassandra-read`.
- Secrets path: `secret/data/netatlas/probe` – contains Hazelcast credentials and SQS access keys.

## Docker‑Compose Setup (local dev)
```yaml
version: "3.8"
services:
  probe:
    image: netatlas/probe:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - VAULT_ROLE=netatlas-probe-role
    ports:
      - "8080:8080"
    depends_on:
      - hazelcast
      - localstack
  hazelcast:
    image: hazelcast/hazelcast:5.2
    ports:
      - "5701:5701"
  localstack:
    image: localstack/localstack:latest
    environment:
      - SERVICES=sqs
    ports:
      - "4566:4566"
```

## Queue Topology
- **Inbound**: `probe.commands` – receives `LockRequestMessage` and other probe jobs.
- **Outbound**: `platform.results.dlq` – captures messages that failed lock acquisition after retries.
- **Fan‑out**: Successful lock acquisition may trigger downstream SNS topics (e.g., `enrich.pipeline`).

## Additional Tips
- Use the `DistributedLockService` bean to test lock behaviour in integration tests.
- Monitor lock metrics via Micrometer (`hazelcast.lock.acquired`, `hazelcast.lock.failed`).
- Ensure the service registers with Consul under the name `probe-service` for health‑aware routing.

---
*Prepared for new team members joining the Device‑Probe service.*
