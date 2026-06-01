# Onboarding Notes: Device-Probe Local Docker-Compose Environment
## Introduction
This document provides notes and instructions for onboarding the Device-Probe local docker-compose environment.

## Consul Paths
* The Consul path for the Device-Probe service is: /device-probe

## Vault Role
* The Vault role for the Device-Probe service is: device-probe-role

## Docker-Compose Setup
* The docker-compose file for the Device-Probe service is: docker-compose.yml

## Queue Topology
* The queue names for the Device-Probe service are: probe.commands, normalize.ingest, enrich.pipeline, platform.results.dlq