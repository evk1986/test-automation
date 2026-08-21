# Test Plan: Staging QA Pass for Cisco IOS-XE NETCONF Subtree Strategy Implementation (PRB-4821)

**Ticket:** TES-193

## Summary
## Description
Validate the newly implemented Cisco IOS-XE NETCONF strategy in the staging environment. Verify error handling, retry limits, and SQS DLQ routing.

## Scope
- Execute test runs against staging Device-Probe cluster for Cisco IOS-XE (ASR, ISR) family
- Validate SQS error handling and DLQ message formation on connection failures

## Acceptance criteria
- All successful probes publish raw responses to probe.commands
- Poison messages and timeout failures land in platform.results.dlq w

## Scope
- Verify Staging QA Pass for Cisco IOS-XE NETCONF Subtree Strategy Implementation (PRB-4821)
- Environment: staging Device-Probe / Data-Enricher cluster

## Pass criteria
- Happy path completes without errors
