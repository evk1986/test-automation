# Test Plan – TES-141 – Data‑Enricher Idempotency Contract

## Summary
Validate that the idempotency‑key contract is correctly exposed via the new REST endpoint and that the underlying Cassandra count reflects stored keys.

## Test Cases
1. **GET /api/v1/enrich/idempotency/info** returns HTTP 200 and a JSON‑compatible string.
2. Mock repository returns a known count; verify response body contains the same number.
3. Verify that the endpoint is secured by the platform’s standard Spring Security filter chain (status 401 when unauthenticated).
4. Load test: invoke the endpoint 100 times concurrently and ensure no exceptions.

## Staging Setup
- Queue: `enrich.pipeline` (already provisioned)
- Cassandra keyspace: `enrich` with table `idempotency_key (key text PRIMARY KEY, created_at timestamp)`
- Actuator health endpoint: `http://staging-enrich.internal:8080/actuator/health`
- Deploy the `data-enricher` service with the new code to the `staging` environment.

## Pass Criteria
- All test cases pass in the staging environment.
- No regression in existing enrichment flows.
- Documentation review approved by the Data‑Enricher lead.
