# Introduction
This document provides onboarding notes for hardening Fleet-Orchestrator batch-config and cron overlap guard.

# Consul paths
* fleet-orchestrator/batch-config
* fleet-orchestrator/cron-overlap-guard

# Vault role
* fleet-orchestrator-role

# Docker-compose setup
* fleet-orchestrator-service

# Queue topology
* probe.commands -> normalize.ingest -> enrich.pipeline -> platform.results.dlq