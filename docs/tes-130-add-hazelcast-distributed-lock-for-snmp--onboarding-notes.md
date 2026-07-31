# Onboarding – Hazelcast Distributed Lock for SNMP Walk (TES‑130)

## Consul Paths
- `service/probe/config/hazelcast/enabled` – boolean flag to turn the lock on/off.
- `service/probe/metrics/snmp/lock` – contains metric definitions for `snmp.lock.acquired` and `snmp.lock.released`.

## Vault Role
- Role name: `netatlas/probe/hazelcast`
- Policies grant read access to `secret/netatlas/hazelcast/*` where cluster credentials are stored.
- The Spring Boot application retrieves the credentials via `spring.cloud.vault` configuration.

## Docker‑Compose Development Setup
```yaml
version: "3.8"
services:
  probe:
    image: netatlas/probe:local
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_CLOUD_VAULT_ENABLED=true
    ports:
      - "8080:8080"
    depends_on:
      - hazelcast
  hazelcast:
    image: hazelcast/hazelcast:5.3
    ports:
      - "5701:5701"
  localstack:
    image: localstack/localstack:2.1
    environment:
      - SERVICES=sqs,sns
    ports:
      - "4566:4566"
```
Run `docker-compose up -d` and verify the probe can acquire a lock against the local Hazelcast node.

## Queue Topology
- **Incoming**: `probe.commands` (SQS) – SNMP walk job requests.
- **Outgoing**: `normalize.ingest` (SNS) – after successful SNMP collection.
- **DLQ**: `platform.results.dlq` – receives messages that could not acquire a lock after retries.

## Helpful Commands
- List Hazelcast members: `docker exec -it $(docker ps -q -f name=hazelcast) bin/hz-cli members list`
- View SQS queue depth (LocalStack): `aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --queue-url http://localhost:4566/queue/probe.commands --attribute-names ApproximateNumberOfMessages`
- Check Micrometer metrics locally: `curl http://localhost:8080/actuator/metrics/snmp.lock.acquired`

## Next Steps for New Team Members
1. Clone the repository and checkout the `feature/tes-130-hazelcast-lock` branch.
2. Run the Docker‑Compose stack.
3. Execute `./mvnw clean test` – all unit tests should pass.
4. Deploy the updated image to a staging namespace and run the integration test suite.
5. Review the Grafana dashboard `netatlas-probe-locks` for lock activity.
