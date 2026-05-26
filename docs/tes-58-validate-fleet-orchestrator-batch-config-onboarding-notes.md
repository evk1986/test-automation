# Onboarding notes for validating Fleet-Orchestrator batch config on staging cron jobs

## Introduction
This document provides onboarding notes for validating the Fleet-Orchestrator batch config on staging cron jobs.

## Prerequisites
* The Fleet-Orchestrator service is deployed to the staging environment
* The probe.commands queue is configured and accessible

## Consul paths
* The Consul path for the Fleet-Orchestrator service is: /services/fleet-orchestrator

## Vault role
* The Vault role for the Fleet-Orchestrator service is: fleet-orchestrator

## Docker-compose setup
* The docker-compose file for the Fleet-Orchestrator service is: docker-compose-fleet-orchestrator.yml

## Queue topology
* The probe.commands queue is configured to receive messages from the Fleet-Orchestrator service
