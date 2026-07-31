# Onboarding – Hazelcast Lock for Device‑Probe (TES‑144)

## Consul Service Paths
- **Service Name**: `hazelcast-probe`
- **Key‑Value Store**: `config/hazelcast/probe`
  - `cpSubsystem.enabled = true`
  - `cpSubsystem.sessionTTLSeconds = 300`
- **Health Check**: `http://{{host}}:5701/hazelcast/health`

## Vault Role & Secrets
- **Role**: `netatlas-probe-hazelcast`
- **Secrets Path**: `secret/data/netatlas/probe/hazelcast`
  - `hazelcast.username`
  - `hazelcast.password`
- Application retrieves credentials via the `VaultTemplate` bean configured in `probe/config/VaultConfig.java`.

## Docker‑Compose / Local Development
```yaml
version: "3.8"
services:
  hazelcast:
    image: hazelcast/hazelcast:5.4
    ports:
      - "5701:5701"
    environment:
      - HZ_CLUSTERNAME=probe-cluster
      - HZ_NETWORK_PUBLICADDRESS=hazelcast:5701
  probe:
    build: ./probe
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - HAZELCAST_CLUSTER_NAME=probe-cluster
      - HAZELCAST_CLUSTER_MEMBERS=hazelcast:5701
    depends_on:
      - hazelcast
```
Run `docker-compose up -d` and verify the lock endpoint with `curl http://localhost:8080/api/v1/probe/locks/<batchId>`.

## Queue Topology
- **Inbound**: `probe.commands` (SQS) – receives `ProbeJobMessage` objects.
- **Outbound**: `normalize.ingest` (SQS) – after successful lock acquisition and data collection.
- **DLQ**: `platform.results.dlq` – receives failed jobs after lock‑related retries exceed the budget.

## Additional Tips
- Use the `hazelcast-client` library version `5.4.2` bundled with the service.
- Enable CP‑Subsystem metrics in `application-dev.yml`:
  ```yaml
  management:
    metrics:
      enable:
        hazelcast: true
  ```
- For debugging, the Actuator endpoint `/actuator/hazelcast` lists active CP locks.
