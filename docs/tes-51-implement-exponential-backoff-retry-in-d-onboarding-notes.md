# Onboarding Notes
## Introduction
This document provides onboarding notes for the implementation of exponential-backoff retry in the Device-Probe NETCONF worker.
## Prerequisites
* The Device-Probe worker is configured to use the test SQS queue.
* The test dead-letter queue is created.
## Steps
1. **Familiarize with the code**: Familiarize yourself with the updated Device-Probe worker code and the exponential-backoff retry implementation.
2. **Understand the test plan**: Understand the test plan and the staging setup.
3. **Run the tests**: Run the tests and verify the results.
4. **Deploy the updated worker**: Deploy the updated Device-Probe worker with the exponential-backoff retry implementation.