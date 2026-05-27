# Onboarding Notes: Implement Hazelcast Distributed Lock in Device-Probe
## Introduction
This document provides onboarding notes for the implementation of a Hazelcast distributed lock in the Device-Probe service to prevent concurrent SNMP walks for the same device-id within the same batch.
## Prerequisites
* Hazelcast instance setup
* Device-Probe service setup
## Consul Paths
* /hazelcast/instance
* /device-probe/service
## Vault Role
* hazelcast-distributed-lock
## Docker-Compose Setup
* hazelcast:latest
* device-probe:latest
## Queue Topology
* probe.commands