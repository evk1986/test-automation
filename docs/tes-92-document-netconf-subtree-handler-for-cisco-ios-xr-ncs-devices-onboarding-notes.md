# Onboarding Notes: Document NETCONF Subtree Handler for Cisco IOS-XR NCS Devices
## Introduction
This document provides onboarding notes for the NETCONF subtree handler for Cisco IOS-XR NCS devices.
## Consul Paths
* /internal/netatlas/probe
## Vault Role
* netatlas-probe
## Docker-Compose Setup
* Use the provided docker-compose.yml file to set up the environment.
## Queue Topology
* probe.commands
* platform.results.dlq