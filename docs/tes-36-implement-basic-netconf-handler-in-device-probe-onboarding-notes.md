# Introduction
This document provides onboarding notes for the implementation of a basic NETCONF handler in Device-Probe for Cisco IOS-XR devices.

# Consul paths
* The Consul path for the Device-Probe service is: /services/device-probe

# Vault role
* The Vault role for the Device-Probe service is: device-probe-role

# Docker-compose setup
* The Docker-compose file for the Device-Probe service is: docker-compose.yml

# Queue topology
* The queue topology for the Device-Probe service is: probe.commands -> SQS queue