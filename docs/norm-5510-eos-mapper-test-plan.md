# Test Plan – Arista EOS eAPI Mapper (NORM-5510)

## Summary
Implement an eAPI mapper that translates raw Arista EOS interface JSON payloads into the platform's `NormalizedRecord`. The mapper lives in `com.internal.netatlas.normalize.mapper` and is exercised by a unit‑test suite covering twelve representative payloads.

## Test Cases
1. **Valid Payload Mapping** – Provide a well‑formed JSON payload; verify that the returned `NormalizedRecord` is non‑null, has `canonicalType` set to `AristaInterface`, and retains the original JSON as `normalizedPayload`.
2. **Missing Optional Fields** – Payload without the `speed` field; mapper should still succeed and preserve whatever fields are present.
3. **Malformed JSON** – Expect an `IOException` to be thrown when the input cannot be parsed.
4. **Batch Coverage** – Execute the test suite against the twelve sample payloads listed in `AristaEosInterfaceMapperNORM5510Test`. All assertions must pass.

## Staging Setup
- **Queue Names** – No queue interaction for this mapper; unit tests run in isolation.
- **Cassandra Table** – `normalized_record` (schema unchanged for this mapper).
- **Actuator Endpoint** – `/actuator/health` must report `UP` before running the test suite.

## Pass Criteria
- `mvn verify` completes with **0** test failures.
- Code coverage reported by JaCoCo shows **≥ 95 %** for `AristaEosInterfaceMapperNORM5510`.
- No unchecked exceptions are logged during test execution.

---
*Ticket: NORM-5510 – Implement Arista EOS eAPI mapper in Schema‑Normalizer*