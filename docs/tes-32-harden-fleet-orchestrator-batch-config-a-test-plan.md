# Summary
This test plan covers the hardening of Fleet-Orchestrator batch-config and cron overlap guard.

# Test cases
1. Test update batch config API endpoint
2. Test cron overlap guard logic

# Staging setup
* Queue names: probe.commands, normalize.ingest, enrich.pipeline, platform.results.dlq
* Cassandra table: batch_config
* Actuator endpoint: /api/v1/orchestrate/batch-config

# Pass criteria
* Batch config is updated successfully
* Cron overlap guard detects overlap and handles it correctly