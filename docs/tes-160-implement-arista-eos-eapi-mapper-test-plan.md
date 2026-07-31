# Test Plan – TES-160 – Implement Arista EOS eAPI Mapper

## Summary
This test plan validates the `AristaEosInterfaceMapperService` which parses Arista EOS *show interfaces* JSON payloads, creates a canonical `InterfaceRecord`, and publishes the result to the SNS topic `enrich.pipeline`.

## Test Cases
1. **Happy path – multiple interfaces**
   - Input JSON contains two interfaces with explicit `operationalStatus` values (`UP` and `DOWN`).
   - Expected: Two SNS publish calls with correctly populated `interfaceName`, `operationalStatus`, `deviceFamily="Arista EOS"`, and `schemaVersion="1.0"`.
2. **Missing operationalStatus**
   - Input JSON contains an interface object without the `operationalStatus` field.
   - Expected: SNS publish call where `operationalStatus` is set to `UNKNOWN` and a warning is logged.
3. **Empty interfaces object**
   - Input JSON has an empty `interfaces` map.
   - Expected: No SNS publish calls and a warning log entry.
4. **Malformed JSON**
   - Input is not valid JSON.
   - Expected: Exception is caught, error logged, and no SNS publish occurs.

## Staging Setup
- **SQS Queue**: `normalize.ingest` (messages containing raw EOS JSON).
- **SNS Topic ARN**: `arn:aws:sns:us-east-1:123456789012:enrich.pipeline`.
- **Cassandra Table**: Not used by this mapper (stateless).
- **Actuator Endpoint**: `http://localhost:8080/actuator/health` – service must be `UP` before running tests.

## Pass Criteria
- All unit tests in `src/test/java/com/internal/netatlas/normalize/service/AristaEosInterfaceMapperServiceTest.java` pass.
- Logs contain the expected warning for missing `operationalStatus`.
- No unhandled exceptions are thrown during processing of the test payloads.
