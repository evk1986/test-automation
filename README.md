# NetAtlas Probe Service

This repository contains the **Device‑Probe** microservice – the entry point of the NetAtlas event‑driven pipeline.  It connects to network devices using a pluggable set of protocol adapters (NETCONF, SSH, SNMP, Arista eAPI, gRPC), collects raw responses and publishes them to the `probe.commands` SQS queue for downstream processing.

## Recent Changes (PRB‑4821)

- **Feature:** Added a dedicated NETCONF subtree handler for Cisco IOS‑XR NCS devices.
- **Classes introduced:**
  - `ProbeHandlersNetconfSubtreeHandler` – SQS listener that filters for NETCONF jobs targeting the IOS‑XR‑NCS family and delegates to the service layer.
  - `ProbeHandlersNetconfSubtreeService` – Builds a minimal NETCONF `<get>` RPC, persists a `DeviceSnapshot` in Cassandra, and updates the corresponding `ProbeJob` status.
  - `DeviceSnapshotRepository` – Cassandra repository for raw snapshots.
- **Tests:** `ProbeHandlersNetconfSubtreeHandlerTest` validates correct routing and guard logic.
- **Documentation:** Updated run‑book and test‑plan markdown files (see `docs/` directory).

## Building & Running

```bash
./mvnw clean package   # Build the jar
docker compose up -d   # Spin up local dependencies (Cassandra, LocalStack for SQS/SNS, Consul)
java -jar target/probe-service-0.0.1-SNAPSHOT.jar
```

## Operational Endpoints

- **Health & Metrics:** `GET /actuator/health`, `GET /actuator/metrics`
- **Job Status (admin):** `GET /api/v1/probe/jobs/{jobId}/status`

## Contact

For questions related to this service, open a ticket with the **PRB** prefix (e.g., `PRB‑4821`).
