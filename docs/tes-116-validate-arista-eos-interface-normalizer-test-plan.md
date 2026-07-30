# Test Plan: Validate Arista EOS Interface Normalizer against golden files (NORM-5510)

**Ticket:** TES-116

## Summary
## Description
Execute regression tests using golden‑file payloads for Arista EOS "show interfaces" to ensure the new mapper produces the expected InterfaceRecord DTOs.
## Scope
- Load six sample payloads from different Arista device families
- Compare mapper output to golden JSON fixtures stored in test/resources
- Verify handling of missing operational‑status fields
## Acceptance criteria
- All six payloads match golden fixtures exactly
- No new failures appear in existing Cisco/NX‑OS test sui

## Scope
- Verify Validate Arista EOS Interface Normalizer against golden files (NORM-5510)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
