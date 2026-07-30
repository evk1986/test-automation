# Onboarding Notes – DLQ Monitoring & Drain (TES‑123)

## Consul Service Registration
- Service name: `orchestrate`
- Key path: `service/orchestrate/config`
- Relevant KV entries:
  - `dlq.mainQueue` = `probe.commands`
  - `dlq.dlqQueue` = `probe.commands.dlq`
  - `dlq.visibilityTimeout` = `30`

## Vault Role & Secrets
- Role: `orchestrate-app`
- Secrets needed:
  - `aws/creds/orchestrate` – provides SQS access keys.
  - `aws/region` – `us-east-1` (used for both main and DLQ queues).
- Retrieve via the Spring Cloud Vault starter; values are injected into `application.yml` under `spring.cloud.vault`.

## Docker‑Compose Development Setup
```yaml
version: "3.8"
services:
  orchestrate:
    image: internal/orchestrate:latest
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
- After `docker compose up`, create the DLQ and main queue using the AWS CLI against the LocalStack endpoint.

## Queue Topology
- **Main Queue**: `probe.commands` – receives job commands from Fleet‑Orchestrator.
- **DLQ**: `probe.commands.dlq` – configured as dead‑letter for the main queue with a max receive count of `5`.
- **Drain Flow**:
  1. `ProbeCommandsDlqHandler` consumes from the DLQ.
  2. `DlqDrainService` processes each message and re‑publishes to `probe.commands` via the internal SNS topic `platform.results`.
  3. Metrics are emitted via Micrometer (`dlq.*`).

## Helpful Commands
- View DLQ messages locally:
  ```bash
  aws sqs receive-message --queue-url http://localhost:4566/000000000000/probe.commands.dlq --max-number-of-messages 10
  ```
- Reset visibility timeout for a specific receipt handle:
  ```bash
  aws sqs change-message-visibility --queue-url http://localhost:4566/000000000000/probe.commands.dlq \
      --receipt-handle <handle> --visibility-timeout 30
  ```
- Trigger the drain endpoint from a shell:
  ```bash
  curl -X POST http://localhost:8080/api/v1/orchestrate/dlq/drain -d '{"sourceQueue":"probe.commands.dlq","targetQueue":"probe.commands"}'
  ```

---
*Prepared for new team members joining the Orchestrate service, 2026‑07‑30.*
