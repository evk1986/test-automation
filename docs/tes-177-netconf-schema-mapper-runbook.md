# Operational Runbook & ADR-0042: NETCONF Handler & Schema Mapper

## Overview
This document covers operational guidance for handling NETCONF timeout surges, draining the `platform.results.dlq`, and architectural decisions regarding pipeline message flow (ADR-0042).

## Runbook: Draining `probe.commands` Dead Letter Queue

### Trigger
Alert `NETCONF_TIMEOUT_SURGE` firing when continuous timeouts on Cisco IOS-XE or Arista EOS devices cause elevated messages in `platform.results.dlq`.

### Remediation Steps
1. Verify Consul health status for Device-Probe instances (`prod-use1`).
2. Inspect AWS SQS metric `ApproximateNumberOfMessagesVisible` on `platform.results.dlq`.
3. Execute the SQS redrive task via management CLI or Fleet-Orchestrator endpoint:
   `curl -X POST http://fleet-orchestrator.prod-use1.internal/api/v1/orchestrate/dlq/redrive -d '{"queue":"platform.results.dlq", "target":"probe.commands"}'`
4. Monitor Cassandra table `probe_jobs` for state changes from `DLQ` to `RUNNING`.

---

## ADR-0042: Probe -> Normalizer -> Enricher Hand-off & Schema Contract

### Context
Introduction of Arista EOS eAPI schema v2.1 require updated canonical mapping rules and explicit schema version tagging across asynchronous pipeline boundaries.

### Sequence Diagram
```
[Device-Probe] ---> (probe.commands SQS) ---> [NetconfCommandHandler]
                                                       |
                                           (Cassandra raw payload)
                                                       |
                                              (normalize.ingest SQS)
                                                       v
                                           [Schema-Normalizer]
                                                       |
                                           (enrich.pipeline SQS)
                                                       v
                                            [Data-Enricher]
                                                       |
                                            (Cassandra & SNS Fan-out)
```

### Async API Contract Version Bump
- Target Service: Schema-Normalizer / Arista EOS Mapper
- Current Version: v2.0
- New Schema Version: v2.1
- Backward Compatibility: Fully backward compatible with v2.0 payload formats; unexpected fields are ingested into generic key-value metadata map.
