# SQS Message Schema Version Bump Runbook
## Introduction
This runbook documents the process for handling an SQS message schema version bump in the Data-Enricher service.
## Prerequisites
* The Data-Enricher service is deployed and running.
* The SQS message schema version has been updated.
## Steps
1. Drain the probe.commands DLQ to prevent any messages from being processed during the schema version bump.
2. Update the Data-Enricher service to use the new SQS message schema version.
3. Verify that the Data-Enricher service is processing messages correctly with the new schema version.
## Troubleshooting
* If issues arise during the schema version bump, refer to the Data-Enricher service logs for error messages.
* If messages are not being processed correctly, verify that the SQS message schema version is correct and that the Data-Enricher service is configured to use the correct version.