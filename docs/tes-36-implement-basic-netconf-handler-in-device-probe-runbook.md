# Runbook for Implementing Basic NETCONF Handler in Device-Probe

## Introduction
This runbook provides a step-by-step guide for implementing a basic NETCONF handler in Device-Probe for Cisco IOS-XR devices.

## Prerequisites
* Cisco IOS-XR device with NETCONF protocol enabled
* Device-Probe service with SQS queue configured

## Steps
1. Connect to Cisco IOS-XR device using NETCONF protocol
2. Run protocol commands and collect raw responses
3. Publish raw response to SQS queue

## Troubleshooting
* Check the Cisco IOS-XR device configuration for NETCONF protocol
* Verify the SQS queue configuration in Device-Probe