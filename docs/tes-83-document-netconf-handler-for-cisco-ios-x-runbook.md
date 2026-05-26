# Runbook for NETCONF Handler for Cisco IOS-XR NCS Devices
## Introduction
This runbook covers the steps for draining the probe.commands DLQ after a vendor firmware mass-timeout event.
## Steps
1. Stop the probe.commands consumer.
2. Drain the probe.commands DLQ.
3. Reset the visibility timeout for the probe.commands DLQ.
## Conclusion
This runbook provides the steps for draining the probe.commands DLQ after a vendor firmware mass-timeout event.