# Summary
Harden Fleet-Orchestrator batch-config and cron overlap guard
## Test cases
1. Test rapid-poll queue depth threshold
2. Test cron overlap guard
## Staging setup
* Queue names: probe.commands, normalize.ingest, enrich.pipeline, platform.results.dlq
* Cassandra table: batch_config
* Actuator endpoint: /api/v1/orchestrator/batch-config
## Pass criteria
* Rapid-poll queue depth threshold is updated correctly
* Cron overlap guard is implemented correctly