# Runbook for validating Fleet-Orchestrator batch config on staging cron jobs

## Introduction
This runbook provides steps to validate the Fleet-Orchestrator batch config on staging cron jobs.

## Prerequisites
* The Fleet-Orchestrator service is deployed to the staging environment
* The probe.commands queue is configured and accessible

## Steps
1. Validate batch config for a valid batch ID
2. Validate batch config for an invalid batch ID
3. Validate batch config for a batch ID with no associated batch config
4. Verify the actuator endpoint returns a 200 OK status code

## Expected outcome
The batch config is validated correctly for all test cases, and the actuator endpoint returns a 200 OK status code.
