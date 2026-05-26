# Onboarding Notes for Implementing Basic NETCONF Handler in Device-Probe

## Introduction
This document provides onboarding notes for implementing a basic NETCONF handler in Device-Probe for Cisco IOS-XR devices.

## Consul Paths
* /device-probe/netconf-handler

## Vault Role
* device-probe-netconf-handler

## Docker-Compose Setup
* device-probe-netconf-handler: latest

## Queue Topology
* probe.commands -> normalize.ingest