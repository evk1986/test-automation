# Onboarding Notes – TES‑157 NETCONF Handler

## Overview
These notes help a new developer get up to speed with the components involved in validating the NETCONF handler for Cisco IOS‑XR NCS devices.

## Consul Service Paths
- `service/device-probe-dev` – registers the Device‑Probe instance.
- `config/device-probe-dev` – holds feature flags such as `netconf.retry.maxAttempts`.
- `kv/netatlas/dev/secrets/vault-role` – Vault role used by the probe pod to obtain credentials.

## Vault Role & Secrets
```text
role: netatlas/dev/probe
policies: [netatlas-dev-read, netatlas-probe-write]
```
The role grants read access to `secret/data/netatlas/dev/probe/*` where the following keys are stored:
- `awsAccessKeyId`
- `awsSecretAccessKey`
- `cassandraUsername`
- `cassandraPassword`

## Docker‑Compose Quick‑Start (local dev)
```yaml
version: "3.8"
services:
  device-probe:
    image: internal.registry.netatlas/device-probe:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - VAULT_ROLE=netatlas/dev/probe
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
Run `docker-compose up -d` and the probe will connect to the local SQS/SNS endpoints.

## Queue Topology
- **probe.commands** – inbound queue where Fleet‑Orchestrator publishes `ProbeJobMessage` objects.
- **normalize.ingest** – SNS topic; probe publishes the snapshot ID after persisting the raw payload.
- **platform.results.dlq** – dead‑letter queue for messages that exceed retry budget.

## Local Testing Tips
1. **Populate the batch** – Use the helper script `scripts/create-ncs-batch.sh BATCH-PRB-20240523-USE1-01` to seed `probe.commands` with mock messages.
2. **Observe metrics** – Access `http://localhost:8080/actuator/prometheus` and filter on `probe_protocol_success`.
3. **Inspect Cassandra** – Run `cqlsh` against the embedded Docker container `cassandra-dev` and query `device_snapshot` and `device_results`.

## Common Pitfalls
- Forgetting to set the `AWS_REGION` environment variable; the probe defaults to `us-east-1` and will not find the dev queues.
- Using the production Consul address in a dev environment – leads to mismatched configuration values.
- Not granting the Vault role `cassandraPassword` permission; the service will fail to connect and log `AuthenticationException`.

## Next Steps
- Run the integration test suite (`./gradlew test`) to ensure the handler behaves as expected.
- Follow the test plan in `docs/tes-157-validate-netconf-handler-for-cisco-ios-xr-ncs-test-plan.md`.
- Once green, create a pull request targeting `dev` branch and request review from the Device‑Probe squad.
