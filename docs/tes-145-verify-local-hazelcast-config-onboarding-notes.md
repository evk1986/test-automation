# Onboarding Notes – TES‑145 – Local Hazelcast & Metric Visibility

## Consul Paths
- `config/device-probe/hazelcast/client` – contains the client XML used in production.
- `config/device-probe/metrics` – defines the Micrometer counter `probe.protocol.failures`.

## Vault Role
- Role name: `device-probe-local`
- Policies: `hazelcast-read`, `cassandra-read`
- Secrets accessed by the service at runtime: `secret/data/device-probe/hazelcast` (cluster name, credentials).

## Docker‑Compose Overview (`docker-compose.probe.yml`)
```yaml
version: "3.8"
services:
  hazelcast:
    image: hazelcast/hazelcast:5.3
    container_name: hazelcast
    ports:
      - "5701:5701"
    environment:
      - HZ_CLUSTER_NAME=probe-cluster
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:5701"]
      interval: 10s
      timeout: 5s
      retries: 5

  device-probe:
    image: netatlas/device-probe:local
    container_name: device-probe
    depends_on:
      hazelcast:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=local
      - HAZELCAST_CLUSTER_NAME=probe-cluster
      - HAZELCAST_NETWORK_PUBLIC_ADDRESS=hazelcast:5701
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 15s
      timeout: 5s
      retries: 3
```

## Queue Topology (local)
- **probe.commands** – SQS mock (localstack) used by the handler to receive `ProbeJobMessage`.
- **normalize.ingest** – downstream queue; not required for this onboarding but present for completeness.

## Steps to Verify
1. Run `docker-compose -f docker-compose.probe.yml up -d`.
2. Confirm both containers are **healthy** (`docker ps`).
3. Execute the SNMP walk endpoint (see runbook) and watch the logs for lock messages.
4. Query the Actuator metric endpoint and ensure the counter appears with the `protocol` tag.
5. Optionally, open the Prometheus UI (`http://localhost:9090`) and search for `probe_protocol_failures_total`.

## Helpful Commands
- `docker logs device-probe` – view Hazelcast client startup and lock logs.
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` – run the service without Docker (uses embedded Hazelcast client pointing to `localhost:5701`).
- `curl http://localhost:8080/actuator/metrics` – list all available metrics.

---
*These notes are intended for new developers onboarding onto the Device‑Probe service. They focus on the local Hazelcast client configuration required for TES‑145.*
