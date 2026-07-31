# Runbook – TES‑145 – Local Hazelcast Configuration Verification

## Purpose
Provide step‑by‑step instructions for developers to spin up a local development environment that mirrors the production Hazelcast lock configuration used by **Device‑Probe** and to confirm that the new Micrometer metric `probe.protocol.failures` is visible to Prometheus.

## Prerequisites
- Docker Desktop (or Docker Engine) installed.
- `docker-compose` version 2.20+.
- Java 17 and Maven 3.9+ installed locally.
- Access to the internal Maven repository for `netatlas/device-probe` artifacts.

## Procedure
1. **Clone the repository**
   ```bash
   git clone https://git.internal.netatlas/device-probe.git
   cd device-probe
   ```
2. **Build the service**
   ```bash
   ./mvnw clean package -DskipTests
   ```
3. **Start Hazelcast and Device‑Probe**
   ```bash
   docker-compose -f docker-compose.probe.yml up -d
   ```
   The compose file defines a single Hazelcast member and the probe service with the following environment variables:
   - `HAZELCAST_CLUSTER_NAME=probe-cluster`
   - `HAZELCAST_NETWORK_PUBLIC_ADDRESS=hazelcast:5701`
   - `SPRING_PROFILES_ACTIVE=local`
4. **Verify container health**
   ```bash
   docker ps --filter "name=hazelcast" --format "{{.Status}}"
   docker logs device-probe
   ```
   Look for the log line `Hazelcast client started and connected to cluster [probe-cluster]`.
5. **Trigger a SNMP walk** (simulated)
   ```bash
   curl -X POST http://localhost:8080/api/v1/probe/jobs/snmp-walk \
        -H "Content-Type: application/json" \
        -d '{"deviceId":"device-001","region":"us-east-1"}'
   ```
   Expected log snippets:
   - `Attempting to acquire distributed lock for job BATCH-PRB-20240523-USE1-01`
   - `Lock acquired – proceeding with SNMP walk`
   - `Lock released after processing`
6. **Check the Micrometer metric**
   ```bash
   curl http://localhost:8080/actuator/metrics/probe.protocol.failures
   ```
   Sample response:
   ```json
   {
     "name": "probe.protocol.failures",
     "measurements": [{"statistic": "COUNT", "value": 1}],
     "availableTags": [{"tag": "protocol", "values": ["SNMP"]}]
   }
   ```
7. **Optional – Verify Prometheus scrape**
   - Open `http://localhost:9090/targets` and ensure the `device-probe` target is up.
   - Query `probe_protocol_failures_total{protocol="SNMP"}` in the Prometheus UI.
8. **Cleanup**
   ```bash
   docker-compose -f docker-compose.probe.yml down -v
   ```

## Troubleshooting
| Symptom | Likely Cause | Remedy |
|---------|--------------|--------|
| `Hazelcast client started` missing | Hazelcast container not reachable or wrong network config | Verify `HAZELCAST_NETWORK_PUBLIC_ADDRESS` matches the service name `hazelcast` and that both containers share the same Docker network. |
| Metric endpoint returns 404 | Actuator metrics not enabled for the local profile | Ensure `management.endpoints.web.exposure.include=health,info,metrics` is present in `application-local.yml`. |
| Lock acquisition logs not present | Distributed lock bean not initialized (Hazelcast client failed) | Check probe logs for `Hazelcast connection failed` and restart the compose stack. |

## Owner
Device‑Probe team – primary contact: **alice.novak@netatlas.internal**
