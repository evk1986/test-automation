# Onboarding Notes for NETCONF Retry Handler
## Introduction
This document provides onboarding notes for the NETCONF retry handler on the staging probe queue.

## Prerequisites
* The staging probe queue is set up and configured.
* The NETCONF retry handler is deployed and configured.

## Consul Paths
* The Consul path for the staging probe queue is: /staging/probe/commands

## Vault Role
* The Vault role for the NETCONF retry handler is: netconf-retry-handler

## Docker-Compose Setup
* The docker-compose file for the staging environment is: docker-compose-staging.yml

## Queue Topology
* The queue topology for the staging probe queue is: probe.commands -> netconf-retry-handler -> probe_jobs