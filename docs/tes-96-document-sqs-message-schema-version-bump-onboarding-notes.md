# Onboarding Notes: SQS Message Schema Version Bump
## Introduction
This document provides onboarding notes for the SQS message schema version bump for the Data-Enricher service.
## Prerequisites
* The Data-Enricher service is deployed and running.
* The SQS message schema version needs to be updated.
## Steps
1. Update the SQS message schema version in the Data-Enricher service.
2. Deploy the updated Data-Enricher service.
3. Verify that the SQS message schema version has been updated successfully.
## Additional Notes
* The SQS message schema version is updated by updating the `enrich.pipeline` queue in the AWS SQS console.
* The updated schema version is used by the Data-Enricher service to process incoming messages.
