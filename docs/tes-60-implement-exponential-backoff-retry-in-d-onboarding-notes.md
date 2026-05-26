# Onboarding Notes: Implement Exponential-Backoff Retry in Device-Probe NETCONF Worker
## Introduction
This document provides onboarding notes for the implementation of exponential-backoff retry in the Device-Probe NETCONF worker.
## Consul Paths
* The Consul path for the Device-Probe service is: /device-probe
## Vault Role
* The Vault role for the Device-Probe service is: device-probe-role
## Docker-Compose Setup
* The docker-compose file for the Device-Probe service is: device-probe-docker-compose.yml
## Queue Topology
* The queue topology for the Device-Probe service is: probe.commands -> platform.results.dlq