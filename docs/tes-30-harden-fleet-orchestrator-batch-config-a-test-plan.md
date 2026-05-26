# Summary
This test plan is designed to verify the functionality of the cron overlap guard feature in the Fleet-Orchestrator service.

# Test cases
1. Test that the cron overlap guard allows a daily sweep job to run when the rapid-poll queue depth is below the threshold and the last daily sweep job was completed more than 24 hours ago.
2. Test that the cron overlap guard prevents a daily sweep job from running when the rapid-poll queue depth is above the threshold.
3. Test that the cron overlap guard prevents a daily sweep job from running when the last daily sweep job was completed less than 24 hours ago.

# Staging setup
* Queue names: rapid-poll-queue, daily-sweep-queue
* Cassandra table: daily_sweep_jobs
* Actuator endpoint: /api/v1/cron-overlap-guard-status

# Pass criteria
* The cron overlap guard allows a daily sweep job to run when the conditions are met.
* The cron overlap guard prevents a daily sweep job from running when the conditions are not met.