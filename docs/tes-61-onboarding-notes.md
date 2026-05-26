# Introduction
This document provides onboarding notes for the NETCONF retry handler on the staging probe queue.

# Consul paths
* The Consul path for the staging probe queue is: /services/probe/queue

# Vault role
* The Vault role for the staging probe queue is: probe-queue-role

# Docker-compose setup
* The docker-compose file for the staging probe queue is: docker-compose-staging-probe-queue.yml

# Queue topology
* The queue topology for the staging probe queue is: probe.commands -> normalize.ingest