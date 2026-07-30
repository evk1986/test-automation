# Onboarding – Arista EOS Interface Normalization

## Consul Paths
- `config/netatlas/normalize/handler/arista-eos` – handler configuration (concurrency, backoff).
- `config/netatlas/normalize/service/arista-eos` – service feature toggle.

## Vault Role
- Role: `netatlas/normalize/arista-eos`
- Secrets: `aws/creds/sqs-normalize`, `aws/creds/sns-publish`

## Docker‑Compose Quick Start
```yaml
version: "3.8"
services:
  normalize:
    image: internal/netatlas-normalize:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - CONSUL_HOST=consul.local
      - VAULT_ADDR=https://vault.local
    ports:
      - "8080:8080"
```

## Queue Topology
- **Input**: `normalize.ingest` (SQS)
- **Output**: `platform.results` (SNS) → subscribed by Data‑Enricher.

## Helpful Commands
```bash
# List queues
aws sqs list-queues --region us-east-1

# Pull a single message for debugging
aws sqs receive-message --queue-url <queue-url> --max-number-of-messages 1
```
