# Summary
Implement and verify the Schema‑Normalizer mapper that converts Arista EOS eAPI **show interfaces** output into the internal `InterfaceRecord` DTO. The mapper is part of ticket NORM‑5510.

# Test cases
1. **Valid payload** – Provide a well‑formed JSON response containing a single interface. Verify all fields are populated correctly in the returned `InterfaceRecord`.
2. **Multiple interfaces** – Payload includes an array of interfaces; mapper should map the first element (current implementation) and not fail.
3. **Missing optional fields** – Omit `description` and ensure the DTO contains an empty string.
4. **Malformed JSON** – Input that cannot be parsed must result in `IllegalArgumentException`.
5. **Integration** – `Norm5510ImplementSchemaNormalizerService.normalizeAristaEosShowInterfaces` forwards the raw JSON to the mapper and returns the same DTO.

# Staging setup
- **Queue**: not used for this mapper (pure transformation).
- **Cassandra table**: `normalized_record` (existing) – mapper does not write directly.
- **Actuator endpoint**: `/actuator/health` must be `UP` before running tests.

# Pass criteria
All JUnit tests in `Norm5510ImplementSchemaNormalizerAristaEosMapperTest` pass, and the service method returns an identical DTO for the valid payload.
