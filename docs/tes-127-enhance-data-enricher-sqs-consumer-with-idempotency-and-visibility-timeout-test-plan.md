# Test Plan – TES‑127 – Enhance Data‑Enricher SQS Consumer

## Summary
This test plan validates the idempotency‑key handling and visibility‑timeout extension introduced for the Data‑Enricher SQS consumer (ENR‑77402). The objectives are to ensure duplicate messages are ignored, the visibility timeout is correctly extended during enrichment, and Micrometer failure metrics are emitted with the required tags.

## Test Cases
1. **Duplicate Message Ignored**
   - **Setup**: Insert an `IdempotencyKey` record with `messageId = "msg-dup"` into the Cassandra table.
   - **Action**: Publish an SQS message with `MessageId = "msg-dup"` to the `enrich.pipeline` queue.
   - **Expected**: Handler logs *Skipping duplicate…* and does **not** invoke the enrichment service nor delete the message.

2. **Visibility Timeout Extension**
   - **Setup**: Mock `SqsClient` to capture `ChangeMessageVisibilityRequest`.
   - **Action**: Send a new message (`MessageId = "msg-new"`).
   - **Expected**: Before enrichment starts, the handler calls `changeMessageVisibility` with a timeout of **300 seconds**. After enrichment completes, it resets the timeout to **30 seconds**.

3. **Failure Counter Emission**
   - **Setup**: Configure the service to throw an exception for a message with protocol `SNMP` and region `us-west-2`.
   - **Action**: Process the message.
   - **Expected**: No delete call, and Micrometer counter `enricher.failures` is incremented with tags `protocol=SNMP` and `region=us-west-2`. The metric is visible at `/actuator/metrics/enricher.failures`.

4. **Successful Processing Path**
   - **Setup**: Normal message (`protocol=NETCONF`, `region=us-east-1`).
   - **Action**: Process the message.
   - **Expected**: Idempotency key persisted, visibility timeout extended, enrichment logic executed, message deleted from the queue, and **no** failure metric increment.

## Staging Setup
- **SQS Queue**: `enrich.pipeline` (standard queue) – ensure the dead‑letter queue `platform.results.dlq` is attached.
- **Cassandra Table**: `enricher.idempotency_keys` with primary key `message_id (text)`.
- **Actuator Endpoint**: `/actuator/metrics/enricher.failures` must be exposed on the Data‑Enricher pod.
- **Spring Profiles**: `staging` – `aws.sqs.enrich.pipeline.url` points to the staging queue URL.

## Pass Criteria
- All four test cases execute without errors.
- Duplicate messages never trigger enrichment logic.
- Visibility timeout changes are observed in the mocked `SqsClient` calls.
- Failure counter appears with correct tags after a simulated exception.
- Maven `verify` phase completes with **0** test failures.
