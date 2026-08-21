# Developer Onboarding Notes: TES-177 NETCONF & Schema Normalizer

## Overview
Overview of pipeline configuration paths and secret mounts for local testing of Device-Probe and Schema-Normalizer.

## Consul Paths
- Key/Value Store Path: `config/device-probe/netconf/timeouts`
- Retry Strategy Path: `config/fleet-orchestrator/retry-budgets`

## HashiCorp Vault Roles
- AppRole Role Name: `device-probe-role`
- Secret Path: `secret/data/telecom/netconf/credentials`

## Local Docker Compose Setup
Start local infrastructure dependencies prior to running handler unit and integration tests:
`docker-compose up -d localstack cassandra hazelcast consul`

## Queue Topology
- Input Queue: `probe.commands`
- Normalization Ingest: `normalize.ingest`
- Enrichment Pipeline: `enrich.pipeline`
- Dead Letter Queue: `platform.results.dlq`
