# Operational Runbook: Hazelcast Lock & Protocol Failure Metric Troubleshooting (PRB-4821)

## Lock Contention & Deadlock Recovery
When high concurrency occurs on rapid polling windows (`POLL-RAPID-77402`), Hazelcast FencedLocks protect Cisco IOS-XE and Arista EOS targets from concurrent SNMP walks.

### Checking Active Locks
Query the Probe administrative endpoint:
```bash
curl -X GET "http://localhost:8081/api/v1/probe/locks-metrics/dev-cisco-asr-01/lock-status"
```

### Verifying Prometheus Failure Metrics
To inspect protocol failures logged during polling cycles:
```bash
curl -s "http://localhost:8081/actuator/prometheus" | grep probe_protocol_failures_total
```

## Alert Triage
- **High Lock Contention Rate:** Indicates queue consumer threads or batch size need tuning in Fleet-Orchestrator.
- **Spike in `probe.protocol.failures`:** Check downstream device reachability, SNMP credentials in Vault, or network routing issues in `prod-use1`.
