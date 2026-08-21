# Operational Runbook: Hazelcast Locking and Protocol Failure Metrics (PRB-4821)

## System Overview
Device-Probe processes rapid polling and daily scheduled discovery batches for network devices (Cisco IOS-XE, Juniper JunOS, Arista EOS). To avoid polling race conditions across multi-node Consul-routed instances, Hazelcast distributed locking serializes operations per device ID.

## Metrics & Monitoring

### Metric Specification
- **Metric Name:** `probe.protocol.failures`
- **Type:** Counter
- **Tags:**
  - `protocol`: Protocol used (`SNMP`, `NETCONF`, `SSH`, `EAPI`, `GRPC`)
  - `region`: Deployment region (`prod-use1`, `prod-usw2`, `dev`, `staging`)
  - `reason`: Failure classification (`LOCK_TIMEOUT`, `INTERRUPTED`, `EXECUTION_FAILED`)

### Prometheus Alert Query Example
```promql
sum(rate(probe_protocol_failures_total[5m])) by (protocol, region) > 10
```

## Troubleshooting & Incident Procedures

1. **High Lock Wait / Lock Acquisition Timeouts**
   - Inspect Hazelcast cluster health via Consul and Actuator health endpoints (`/actuator/health`).
   - Check Hazelcast CP Subsystem metrics to ensure raft quorum is intact.

2. **Spike in Protocol Failure Metric**
   - Query Prometheus for `probe.protocol.failures` grouped by `reason`.
   - If `reason="LOCK_TIMEOUT"`, verify batch sizing and worker concurrency settings in Fleet-Orchestrator.
