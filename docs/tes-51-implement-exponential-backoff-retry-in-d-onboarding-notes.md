# Onboarding Notes for Implementing Exponential Backoff Retry in Device-Probe NETCONF Worker
## Introduction
This document provides onboarding notes for implementing exponential backoff retry in the Device-Probe NETCONF worker.
## Consul Paths
* The Consul path for the Device-Probe worker is: /device-probe/worker
## Vault Role
* The Vault role for the Device-Probe worker is: device-probe-worker
## Docker-Compose Setup
* The docker-compose file for the Device-Probe worker is: device-probe-worker/docker-compose.yml
## Queue Topology
* The queue topology for the Device-Probe worker is: probe.commands -> platform.results.dlq