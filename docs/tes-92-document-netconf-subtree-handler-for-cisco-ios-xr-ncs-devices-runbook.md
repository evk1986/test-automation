# Runbook: Draining probe.commands DLQ after vendor firmware mass-timeout event
## Introduction
This runbook provides steps to drain the probe.commands DLQ after a vendor firmware mass-timeout event.
## Steps
1. Identify the affected devices and their corresponding probe jobs.
2. Drain the probe.commands DLQ by sending a delete message for each affected probe job.
3. Reset the visibility timeout for each affected probe job.
## SQS Message Schema Version Bump
After draining the DLQ, update the SQS message schema version to reflect the changes.
## Conclusion
By following these steps, you can effectively drain the probe.commands DLQ and handle schema version bumps after a vendor firmware mass-timeout event.