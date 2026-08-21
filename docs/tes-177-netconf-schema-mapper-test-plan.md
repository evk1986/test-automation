# Test Plan: TES-177 - NETCONF Handler & Schema Mapper Operational Procedures

## Summary
Validates the NETCONF command handler retry mechanics, DLQ replay procedures, Arista EOS schema mapping, and pipeline propagation for tickets PRB-4821 and NORM-5510.

## Test cases
1. High-latency NETCONF timeouts triggered on Cisco IOS-XE devices move failed jobs to platform.results.dlq after exhausting retry attempts.
2. Manual invocation of the DLQ drain utility replays messages back to probe.commands without duplicating job executions.
3. Arista EOS eAPI schema mapper handles payload schema version 2.1 canonical transformation and maps interfaces cleanly to normalize.ingest.
4. Schema version compatibility matrix prevents incompatible schema versions from advancing to enrich.pipeline.

## Staging setup
- SQS Queues: probe.commands, normalize.ingest, enrich.pipeline, platform.results.dlq
- Cassandra Table: device_snapshots, probe_jobs
- Actuator Endpoint: http://localhost:8080/actuator/health, http://localhost:8080/actuator/metrics/sqs.consumed

## Pass criteria
- Zero lost records during simulated NETCONF timeout surge.
- DLQ drain completes within retry SLA without manual database intervention.
- All Arista EOS payloads map successfully under schema version v2.1.
