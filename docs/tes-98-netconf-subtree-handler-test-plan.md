# Test Plan – TES-98 NETCONF Subtree Handler

## Summary
Implements a new SQS listener `ProbeHandlersNetconfJobDispatcher` that routes NETCONF
subtree jobs to `NetconfSubtreeService`. A Micrometer counter `netconf.subtree.dispatch`
tracks dispatch volume. The service uses an increased timeout (10 s) to mitigate the
previous intermittent timeout issue.

## Test Cases
1. **Dispatch Counter Increment**  
   - Send a `ProbeJobMessage` with protocol `NETCONF`.  
   - Verify the service method is invoked and the Micrometer counter increments to `1`.

2. **Non‑NETCONF Message Ignored**  
   - Send a message with protocol `SNMP`.  
   - Verify no service call occurs and the counter remains unchanged.

3. **Successful Job Persistence**  
   - Mock `NetconfSubtreeJobRepository` to capture `saveJob` arguments.  
   - Ensure a `SUCCESS` status is recorded when processing completes without interruption.

4. **Interrupted Execution Path**  
   - Simulate an `InterruptedException` during processing.  
   - Verify the repository records a `FAILED` status.

## Staging Setup
- **SQS Queue**: `probe.commands` (standard queue)  
- **Cassandra Table**: `netconf_subtree_jobs` (created by `NetconfSubtreeJobRepository`)  
- **Actuator Endpoint**: `GET /actuator/metrics/netconf.subtree.dispatch` – should expose the counter.  
- **Metrics Backend**: Prometheus scrapes `/actuator/prometheus`.

## Pass Criteria
- All unit and integration tests pass (`mvn verify`).  
- Counter metric appears in Prometheus with expected values after test execution.  
- No timeout‑related errors in logs when processing simulated jobs.
