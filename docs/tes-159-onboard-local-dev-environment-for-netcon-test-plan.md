# Test Plan: Onboard Local Dev Environment for NETCONF Handler (TES-159)

## Summary
Validate that the local Docker‑Compose stack can start the Device‑Probe service, that the
`LocalDevSetupNetconfHandler` correctly receives a NETCONF job from the mock SQS queue,
delegates to `LocalDevSetupNetconfService`, and that the service parses a sample XML payload
into an `InterfaceRecord` DTO. Verify health endpoints.

## Test Cases
1. **Start stack** – Run `docker-compose up -d`. Expect all containers (probe, consul, local‑sqs‑mock) to be healthy.
2. **Vault role configuration** – Export `VAULT_ROLE_ARN=arn:aws:iam::123456789012:role/dev-netconf-probe`. Verify the probe logs “Vault role ARN loaded”.
3. **Send sample NETCONF message** – Publish a JSON message to `probe.commands` queue with protocol `NETCONF` and a simple interface XML payload.
4. **Handler execution** – Observe log entry `Processing NETCONF job for device‑123`. Ensure the service returns an `InterfaceRecord` with name `GigabitEthernet0/0/0`.
5. **Health check** – `curl http://localhost:8080/actuator/health`. Expect JSON with `"status":"UP"` for all components.

## Staging Setup
- **Queue**: `probe.commands` (local SQS mock)
- **Cassandra keyspace**: `dev_netatlas` with table `probe_job` (schema created by migrations)
- **Consul**: Agent runs at `http://localhost:8500`. Verify registration via `curl http://localhost:8500/v1/agent/services`.
- **Vault**: Role ARN injected via environment variable; mock Vault returns a dummy token.

## Pass Criteria
- Docker‑Compose stack reports `healthy` for all services.
- The handler processes the message without exception and the service returns a non‑null `InterfaceRecord`.
- `/actuator/health` returns status `UP`.
- No entries appear in the DLQ queue.
