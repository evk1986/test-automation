# Runbook for Validating Schema-Normalizer Golden-File Tests
## Introduction
This runbook provides steps for validating Schema-Normalizer golden-file tests for Arista EOS devices.

## Prerequisites
* Schema-Normalizer service is running
* Arista EOS devices are configured

## Steps
1. Receive messages from normalize.ingest queue
2. Validate normalized records against golden file
3. Log results