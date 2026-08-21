# Onboarding Notes: Probe Lock & Operational Metrics Integration

## Overview
This guide outlines the development environment setup for testing Hazelcast distributed locking and Micrometer Prometheus metrics integration within Device-Probe.

## Configuration Paths
- **Consul KV Path:** `config/device-probe/dev/hazelcast`
- **Vault Role:** `probe-service-role`
- **Active Queues:** `probe.commands`, `platform.results.dlq`

## Local Environment Startup
Start local stack services including Hazelcast and Cassandra:
```bash
docker-compose up -d hazelcast cassandra localstack
```

Verify Spring Boot Actuator endpoint locally:
`http://localhost:8081/actuator/prometheus`
