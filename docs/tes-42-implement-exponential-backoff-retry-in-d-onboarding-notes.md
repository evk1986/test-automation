# Onboarding Notes: Implement Exponential-Backoff Retry in Device-Probe NETCONF Worker
## Introduction
This document provides onboarding notes for the implementation of exponential-backoff retry in the Device-Probe NETCONF worker.

## Consul Paths
* /services/device-probe
* /services/netconf-retry-service

## Vault Role
* device-probe-netconf-retry

## Docker-Compose Setup
* device-probe: latest
* netconf-retry-service: latest

## Queue Topology
* probe.commands
* platform.results.dlq