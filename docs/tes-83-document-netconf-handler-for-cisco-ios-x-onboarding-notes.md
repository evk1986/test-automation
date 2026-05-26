# Onboarding Notes for NETCONF Handler for Cisco IOS-XR NCS Devices
## Introduction
This document provides the onboarding notes for the NETCONF handler for Cisco IOS-XR NCS devices.
## Consul Paths
* /probe/commands
* /enrich/pipeline
## Vault Role
* netatlas-probe
## Docker-Compose Setup
* probe-commands-consumer
* enrich-pipeline-producer
## Queue Topology
* probe.commands -> enrich.pipeline