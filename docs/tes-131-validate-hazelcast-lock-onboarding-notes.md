# Onboarding Notes – TES-131 – Hazelcast Lock for SNMP Walk Serialization

## Overview
These notes help a new developer understand the components involved in the Hazelcast lock implementation for the **Device‑Probe** service and how to run the associated integration tests locally.

## Consul Service Discovery
- **Path**: `netatlas/probe/hazelcast`
- **Keys**:
  - `hazelcast.cluster-name` – name of the Hazelcast cluster (e.g., `netatlas-hz`)
  - `hazelcast.members` – comma‑separated list of member addresses used by the client bean.

## Vault Role
- **Role Name**: `netatlas-device-probe`
- Grants read access to `secret/data/netatlas/hazelcast` where TLS keystore passwords are stored.
- The Spring bean `HazelcastConfig` pulls the credentials via `VaultTemplate`.

## Docker‑Compose Development Stack
```yaml
version: "3.8"
services:
  hazelcast:
    image: hazelcast/hazelcast:5.3
    ports:
      - "5701:5701"
    environment:
      - HZ_CLUSTER_NAME=netatlas-hz
  cassandra:
    image: cassandra:4.0
    ports:
      - "9042:9042"
  localstack:
    image: localstack/localstack:2.2
    environment:
      - SERVICES=sqs,sns
    ports:
      - "4566:4566"
```
Run with `docker-compose up -d`.

## Running the Integration Test Suite
1. Start the Docker stack above.
2. Ensure the Maven profile `local` points to the local Hazelcast and Cassandra endpoints.
3. Execute:
   ```bash
   ./mvnw test -Dtest=IntegrationTestSuiteForSnmp
   ```
   The test will mock the Hazelcast client but still validates the lock‑key naming convention.

## Key Files
- `SnmpWalkLockService.java` – contains the lock acquisition logic.
- `SnmpWalkJobHandler.java` – SQS listener that forwards messages to the service.
- `IntegrationTestSuiteForSnmp.java` – unit tests covering lock lifecycle and handler delegation.

## Further Reading
- [Spring Cloud AWS SQS Listener Documentation](https://docs.spring.io/spring-cloud-aws/docs/current/reference/html/#sqs-listeners)
- [Hazelcast Distributed Lock Best Practices](https://hazelcast.com/blog/distributed-locking-patterns/)
