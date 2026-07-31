# PRB-874 Onboarding Notes – Verify Local Hazelcast Config & Metric Visibility

## Summary
This document guides a developer through verifying the local Hazelcast client configuration used by the **Device‑Probe** service and confirming that the related Micrometer metric is exposed on the Prometheus endpoint.

## Consul Paths
- `config/device-probe/hazelcast` – contains the client configuration (cluster name, network settings).
- `service/device-probe/health` – health check endpoint registered via Spring Boot Actuator.

## Vault Role
- Role: `device-probe-app`
- Secrets path: `secret/data/device-probe/hazelcast`
- The application obtains the Hazelcast password at startup via the `hazelcast.client.password` property.

## Docker‑Compose Setup (local)
```yaml
version: "3.8"
services:
  hazelcast:
    image: hazelcast/hazelcast:5.3
    ports:
      - "5701:5701"
    environment:
      - HZ_CLUSTERNAME=dev-cluster
  device-probe:
    build: ./device-probe
    depends_on:
      - hazelcast
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - HAZELCAST_CLIENT_CLUSTER_NAME=dev-cluster
      - HAZELCAST_CLIENT_NETWORK_ADDRESSES=hazelcast:5701
```
Run `docker compose up -d` and wait for both containers to become healthy.

## Queue Topology
- **Input Queue**: `probe.commands` (SQS)
- **DLQ**: `platform.results.dlq`
- **Metrics**: Published to `/actuator/prometheus` on port `8080`.

## Verification Steps
1. **Start the stack** using the Docker‑Compose file above.
2. Execute the Spring Boot bean manually or via a test endpoint:
   ```java
   @Autowired
   LocalHazelcastVerificationService verifier;
   boolean ok = verifier.verifyLocalHazelcastConfig();
   ```
   The log should show the size of the `device-probe-locks` map.
3. Open a browser or curl the Prometheus endpoint:
   ```
   curl http://localhost:8080/actuator/prometheus | grep hazelcast_map_device_probe_locks_size
   ```
   You should see a line similar to:
   ```
   hazelcast_map_device_probe_locks_size 3.0
   ```
4. If the metric is missing, ensure that the `MeterRegistry` bean is correctly injected and that the Hazelcast client can reach the cluster (check Consul DNS resolution and Vault secret retrieval).

## Pass Criteria
- The `LocalHazelcastVerificationService.verifyLocalHazelcastConfig()` method returns `true`.
- The log contains an INFO entry with the lock map size.
- The Prometheus endpoint exposes the gauge `hazelcast_map_device_probe_locks_size` with a numeric value.
- No exceptions are thrown during verification.

## Cleanup
```bash
docker compose down
```
Remove any local Hazelcast data directories if they were mounted as volumes.
