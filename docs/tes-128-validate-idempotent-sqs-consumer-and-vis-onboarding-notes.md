# Onboarding – Idempotent SQS Consumer (ENR‑77402)

## Consul Service Registration
- Service name: `data-enricher`
- Health check URL: `http://{{HOST_IP}}:8080/actuator/health`
- Tags: `staging`, `enrich`, `sqs-consumer`

## Vault Role
- Role name: `data-enricher-staging`
- Policies attached: `cassandra-readwrite`, `sqs-access`, `cloudwatch-logs-read`
- AWS IAM auth path: `auth/aws/login`

## Docker‑Compose (local dev) Snapshot
```yaml
version: "3.8"
services:
  data-enricher:
    image: repo/data-enricher:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - AWS_REGION=us-east-1
      - SQS_QUEUE=enrich.pipeline
      - CASSANDRA_CONTACT_POINTS=cassandra:9042
    ports:
      - "8080:8080"
    depends_on:
      - cassandra
  cassandra:
    image: cassandra:4.0
    ports:
      - "9042:9042"
```

## Queue Topology
- **Input Queue**: `enrich.pipeline` (standard SQS)
- **Dead‑Letter Queue**: `platform.results.dlq`
- **Fan‑out**: After successful enrichment, messages are published to SNS topic `platform.results` (not part of this test).

## Helpful Commands
- List SQS messages (staging):
  ```bash
  aws sqs receive-message --queue-url https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline --max-number-of-messages 10
  ```
- View Micrometer metrics:
  ```bash
  curl http://localhost:8080/actuator/metrics | jq .
  ```
- Check Hazelcast map for in‑flight batch IDs:
  ```bash
  curl http://localhost:5701/hazelcast/rest/maps/batch-status
  ```

---
*Onboarding notes compiled for ticket TES‑128.*
