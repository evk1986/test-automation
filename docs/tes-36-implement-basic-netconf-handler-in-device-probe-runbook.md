# Introduction
This runbook is for the implementation of a basic NETCONF handler in Device-Probe for Cisco IOS-XR devices.

# Prerequisites
* The Device-Probe service is installed and configured.
* The Cisco IOS-XR device is configured and accessible.

# Steps
1. Connect to the Cisco IOS-XR device using the NETCONF protocol.
2. Execute protocol commands and collect raw responses.
3. Publish raw responses to the SQS queue.

# Troubleshooting
* Check the connection to the Cisco IOS-XR device.
* Check the execution of protocol commands and collection of raw responses.
* Check the publishing of raw responses to the SQS queue.