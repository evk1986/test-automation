# Runbook for NETCONF Retry Handler
## Introduction
This runbook provides a step-by-step guide for deploying and configuring the NETCONF retry handler on the staging probe queue.

## Prerequisites
* The staging probe queue is set up and configured.
* The NETCONF retry handler is deployed and configured.

## Deployment Steps
1. Deploy the NETCONF retry handler to the staging environment.
2. Configure the staging probe queue to trigger the NETCONF retry handler.

## Verification Steps
1. Verify that the NETCONF retry handler is triggered and the job is retried.
2. Verify that the job status is updated to "RUNNING".