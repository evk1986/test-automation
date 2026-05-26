# Onboarding Notes: NETCONF Handler
## Introduction
This document provides notes for onboarding the NETCONF handler.
## Consul Paths
* /netatlas/probe/health
* /netatlas/probe/metrics
## Vault Role
* netatlas-probe
## Docker-Compose Setup
* netatlas-probe: latest
## Queue Topology
* probe.commands -> normalize.ingest