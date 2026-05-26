# Onboarding notes
This document provides notes for onboarding the Device-Probe service with exponential-backoff retry.

## Consul paths
* The Consul path for the Device-Probe service is: /services/device-probe

## Vault role
* The Vault role for the Device-Probe service is: device-probe-role

## Docker-compose setup
* The docker-compose file for the Device-Probe service is: device-probe/docker-compose.yml

## Queue topology
* The queue topology for the Device-Probe service is: probe.commands -> platform.results.dlq
