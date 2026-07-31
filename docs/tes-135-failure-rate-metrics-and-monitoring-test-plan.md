# Test Plan for TES‑135 Failure‑Rate Metrics and Monitoring

## Summary
Validate that the monitoring runbook endpoint returns the expected markdown and that the service correctly retrieves the runbook content.

## Test Cases
1. **GET /api/v1/monitoring/runbook** returns HTTP 200 and a non‑empty body.
2. Service layer returns fallback text when repository returns empty.
3. Repository `findRunbook` returns a static markdown containing sections *Overview*, *Alerts*, and *Troubleshooting*.

## Staging Setup
- Queue names: none (REST only).
- Cassandra table: `runbook` (simulated in repository stub).
- Actuator endpoint: `/actuator/health` should be reachable.

## Pass Criteria
All test cases pass in the `tes-135` staging profile and code coverage for the controller is ≥ 80 %.
