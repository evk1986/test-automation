# Test Plan: NORM-5510 Arista EOS eAPI Interface Mapper & v3 Schema

## Summary
Validates parsing of Arista EOS eAPI `show interfaces` output and transformation into canonical `InterfaceRecord` DTOs (Schema v3) within `Schema-Normalizer` across all supported device families.

## Test Cases
1. **Arista EOS Standard eAPI Output**: Verify parsing of Arista 7050 and 7280 raw JSON responses.
2. **Missing Operational Status**: Ensure fallback to `unknown` without throwing null pointer or runtime exceptions when `lineProtocolStatus` or `interfaceStatus` is absent.
3. **Cisco IOS-XE Support**: Validate processing of Cisco IOS-XE interfaces payload.
4. **Cisco IOS-XR Support**: Validate processing of Cisco IOS-XR interfaces payload.
5. **Cisco NX-OS Support**: Validate processing of Cisco NX-OS Nexus interfaces payload.
6. **Juniper JunOS & Generic SNMP CPE**: Validate normalization across Juniper JunOS and CPE devices.

## Staging Setup
- **SQS Ingest Queue**: `normalize.ingest`
- **Cassandra Persistence Table**: `normalized_records`
- **Actuator Endpoint**: `/actuator/health`
- **Metrics Endpoint**: `/actuator/prometheus`

## Pass Criteria
- All 6 supported device families process successfully.
- Schema version set to `v3` on all emitted canonical records.
- 0 runtime exceptions on missing payload fields.
- Test coverage exceeding 90%.