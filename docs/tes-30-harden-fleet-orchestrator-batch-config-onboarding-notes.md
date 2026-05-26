# Introduction
This document provides onboarding notes for the cron overlap guard feature in the Fleet-Orchestrator service.

# Consul paths
* The Fleet-Orchestrator service is registered in Consul under the path /fleet-orchestrator.

# Vault role
* The Fleet-Orchestrator service uses the vault role fleet-orchestrator to authenticate with Vault.

# Docker-compose setup
* The Fleet-Orchestrator service can be run using the docker-compose file provided in the repository.

# Queue topology
* The rapid-poll queue and daily sweep queue are configured using the AWS SQS console.