# Runbook for Device-Probe local development environment
## Introduction
This runbook provides instructions for setting up and running the Device-Probe local development environment.

## Prerequisites
* Docker and docker-compose installed
* Vault IAM role ARN and Consul agent health-check port noted

## Setup
1. Clone the Device-Probe repository.
2. Run `docker-compose up` to start the services.
3. Verify that the Device-Probe service is running and responding to requests.