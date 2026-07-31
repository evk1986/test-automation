# Onboarding Notes – NETCONF Handler Deployment (TES‑138)

## Consul Service Paths
- **Service ID**: `probe.handlers.netconf`
- **Health Check**: `http://{{host}}:8080/actuator/health`
- **Config Key**: `netatlas/probe/handlers/netconf`

## Vault Role & Secrets
- **Role**: `netatlas-probe`
- **Secrets Path**: `secret/probe/netconf`
  - `username`
  - `password`
  - `sshKey`
- Retrieve via Spring Cloud Vault configuration (`spring.cloud.vault` properties).

## Docker‑Compose Development Setup
```yaml
version: "3.8"
services:
  probe-service:
    image: netatlas-probe:tes-138
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - HAZELCAST_LOCK_NAME=netconfHandlerLock
    ports:
      - "8080:8080"
    depends_on:
      - hazelcast
      - localstack
  hazelcast:
    image: hazelcast/hazelcast:5.3
    ports:
      - "5701:5701"
  localstack:
    image: localstack/localstack:latest
    environment:
      - SERVICES=sqs,sns
    ports:
      - "4566:4566"
```

## Queue Topology
- **Incoming**: `probe.commands` (standard SQS) – messages produced by Fleet‑Orchestrator.
- **Dead‑Letter**: `platform.results.dlq` – receives failed NETCONF jobs; monitored by the runbook rollback steps.
- **Outbound**: (not used in this slice) – normally publishes to `normalize.ingest` SNS.

## Helpful Commands
- List SQS queues:
  ```bash
  aws sqs list-queues --region us-east-1
  ```
- View Hazelcast cluster members:
  ```bash
  curl http://localhost:5701/hazelcast/rest/cluster
  ```
- Check Spring Actuator health:
  ```bash
  curl http://localhost:8080/actuator/health
  ```

---
*These notes are intended for developers onboarding the NETCONF handler slice of the Device‑Probe service.*