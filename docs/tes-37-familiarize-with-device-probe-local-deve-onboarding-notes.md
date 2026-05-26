# Onboarding notes for Device-Probe local development environment
## Introduction
This document provides notes for onboarding the Device-Probe local development environment.

## Consul paths
* The Consul agent health-check port is 8500.

## Vault role
* The Vault IAM role ARN is arn:aws:iam::123456789012:role/DeviceProbeRole.

## Docker-compose setup
* The docker-compose file is located in the root of the repository.

## Queue topology
* The probe.commands queue is used for sending commands to devices.
* The normalize.ingest queue is used for ingesting normalized data.