# Onboarding Notes: Onboard rapid-poll scaling config to local dev environment (ORCH-882)

**Ticket:** TES-155

## Summary
## Description
Set up local Docker‑Compose stack to include the new rapid‑poll scaling configuration and verify metric exposure.
## Scope
- Add Vault IAM role ARN for config secret
- Update Consul service registration with new config key
- Run local probe.commands queue emulator and confirm gauge appears
## Acceptance criteria
- Developer can start the stack and see `fleet.orchestrator.probe.commands.depth` metric locally
- No startup errors related to the new config

## Scope
- Verify Onboard rapid-poll scaling config to local dev environment (ORCH-882)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
