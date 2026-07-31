# Summary
Test plan for Arista EOS interface normalization (NORM-5510) ensuring schema version v3 is applied.

## Test Cases
1. **Valid CSV payload** – Send a message `"Ethernet1,up,1000Mbps"` to `normalize.ingest`. Verify the handler logs normalized JSON with fields `interfaceName`, `adminStatus`, `speed`, and `schemaVersion` set to `v3`.
2. **Malformed payload** – Send `"invalid_payload"` and verify the service returns JSON containing the original `raw` field and `schemaVersion` `v3`.
3. **SNS publishing stub** – Ensure no exception is thrown when the handler completes processing (publishing step is a no‑op in test).

## Staging Setup
- **Queue**: `normalize.ingest` (standard SQS)
- **Cassandra table**: `normalized_record` (schema version column)
- **Actuator endpoint**: `GET /actuator/health` must report `UP`.

## Pass Criteria
All automated unit tests pass, and manual injection of messages into the staging queue results in correctly formatted JSON records stored in Cassandra without errors.
