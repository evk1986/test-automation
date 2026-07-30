# Onboarding – NETCONF Handler Integration (TES‑99)

## Consul Service Paths
- `service/device-probe-staging` – registers each probe pod with health checks `/actuator/health`.
- `config/netconf` – holds NETCONF timeout and retry settings (retrieved via Spring Cloud Consul Config).

## Vault Role
- Role name: `netatlas/probe/staging`
- Policies grant read access to `secret/netatlas/probe/netconf/*` where device credentials are stored.
- Applications authenticate using AWS IAM role `arn:aws:iam::123456789012:role/staging-probe-vault`.

## Docker‑Compose (Local Development)
```yaml
version: "3.8"
services:
  probe:
    image: netatlas/device-probe:2.7.7-netconf-v1
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - VAULT_ROLE=netatlas/probe/dev
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

## Queue Topology
- **Inbound**: `probe.commands` – SQS queue where Fleet‑Orchestrator places `ProbeJobMessage` objects.
- **Outbound**: `normalize.ingest` – SQS queue consumed by the Schema‑Normalizer service.
- **DLQ**: `platform.results.dlq` – captures messages that could not be processed after retries.
- All queues are FIFO, encrypted at rest, and use a visibility timeout of 30 seconds.

## Helpful Commands
- List Consul services: `consul catalog services`
- Retrieve Vault secret: `vault kv get secret/netatlas/probe/netconf/credentials`
- View SQS queue depth: `aws sqs get-queue-attributes --queue-url <url> --attribute-names ApproximateNumberOfMessages`

---
*Onboarding notes compiled for ticket **PRB‑4821** – 2026‑06‑04.*
