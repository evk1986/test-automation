# Onboarding notes: Harden Fleet-Orchestrator batch-config and cron overlap guard
## Introduction
This document provides onboarding notes for hardening Fleet-Orchestrator batch-config and cron overlap guard.
## Consul paths
* /fleet-orchestrator/batch-config
## Vault role
* fleet-orchestrator
## Docker-compose setup
* fleet-orchestrator:latest
## Queue topology
* probe.commands -> normalize.ingest -> enrich.pipeline -> platform.results.dlq