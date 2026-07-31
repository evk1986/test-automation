# Onboarding Notes – Hazelcast Lock Serialization Test (TES-143)

## Consul Paths
- `config/netatlas/probe/hazelcast` – Hazelcast cluster configuration (multicast disabled, TCP/IP members list).
- `services/probe` – Service registration for Device‑Probe; health checks point to `/actuator/health`.

## Vault Role
- Role name: `netatlas-probe-role`
- Policies: `netatlas/probe/*` – grants read access to `secret/netatlas/probe/*` where AWS credentials for SQS are stored.

## Docker‑Compose (Local Development)
```yaml
version: "3.8"
services:
  hazelcast:
    image: hazelcast/hazelcast:5.3
    ports:
      - "5701:5701"
  probe-service:
    build: ./probe-service
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - HAZELCAST_CLUSTER_NAME=netatlas-cluster
      - HAZELCAST_NETWORK_PUBLICADDRESS=hazelcast:5701
    depends_on:
      - hazelcast
```

## Queue Topology
- **Incoming**: `probe.commands` (SQS FIFO not required).
- **Outgoing**: `normalize.ingest` (SNS topic) – not used in this test.
- **DLQ**: `platform.results.dlq` – messages are routed here only on unrecoverable failures.

## Running the Integration Test Locally
```bash
./gradlew test --tests com.internal.netatlas.probe.handler.IntegrationTestSuiteSqsReplayHandlerTest
```
The test starts an embedded Hazelcast instance and a SimpleMeterRegistry, publishes mock `ProbeJobMessage` objects, and asserts lock behavior.

---
*Onboarding prepared for ticket TES-143 (PRB-874).*