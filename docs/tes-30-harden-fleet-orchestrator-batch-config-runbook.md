# Introduction
This runbook provides a step-by-step guide to implementing the cron overlap guard feature in the Fleet-Orchestrator service.

# Prerequisites
* The Fleet-Orchestrator service is deployed and running.
* The rapid-poll queue and daily sweep queue are configured.

# Steps
1. Update the Fleet-Orchestrator service to include the cron overlap guard feature.
2. Configure the rapid-poll queue depth threshold.
3. Configure the daily sweep job completion time threshold.
4. Test the cron overlap guard feature using the test plan.

# Troubleshooting
* If the cron overlap guard is not working as expected, check the logs for errors.
* If the rapid-poll queue depth is above the threshold, check the queue configuration and adjust as needed.