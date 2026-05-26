# Onboarding notes
Validate NETCONF retry handler on staging probe queue.

## Consul paths
* /probe/commands

## Vault role
* netatlas-probe

## Docker-compose setup
* docker-compose up -d

## Queue topology
* probe.commands -> netconf-retry-handler -> cassandra