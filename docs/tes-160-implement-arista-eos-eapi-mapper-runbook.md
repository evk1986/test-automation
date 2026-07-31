# Runbook – Arista EOS eAPI Mapper (TES-160)

## Purpose
The mapper converts raw Arista EOS *show interfaces* responses into the platform‑wide canonical `InterfaceRecord` and forwards them to the enrichment pipeline.

## Operational Steps
1. **Verify Service Health**
   - Call `GET /actuator/health` on the Schema‑Normalizer instance.
   - Status must be `"status":"UP"`.
2. **Inspect Queue Depth**
   - Use AWS Console or CLI to check the number of messages in `normalize.ingest`.
   - If the depth exceeds the threshold (10,000), consider scaling the consumer pods.
3. **Review Recent Logs**
   - Search for `AristaEosInterfaceMapperService` entries.
   - Look for warnings about missing `operationalStatus` and errors indicating JSON parsing failures.
4. **Manual Re‑process (if needed)**
   - Retrieve a message from the queue using `aws sqs receive-message`.
   - Run the handler locally: `java -cp ... com.internal.netatlas.normalize.handler.AristaEosInterfaceMapperHandler` passing the payload.
   - Confirm that an SNS message appears in `enrich.pipeline` (use `aws sns list-subscriptions-by-topic`).
5. **Rollback**
   - If unexpected failures occur, disable the SQS listener by setting the Spring profile `mapper.disabled=true` and redeploy.
   - Re‑enable after fixing the root cause.

## Alerting
- **Metric**: `schema_normalizer.mapper.errors` (Micrometer counter).
- **Alert**: Trigger when the error rate exceeds 5 % over a 5‑minute window.

## Contact
- Primary Owner: *Backend Engineer – TES-160*
- On‑call: *Platform Operations* (PagerDuty `netatlas-oncall`).
