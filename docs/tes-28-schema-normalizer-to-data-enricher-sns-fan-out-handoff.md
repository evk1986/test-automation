# Architecture Decision Record: Schema-Normalizer to Data-Enricher SNS Fan-out Handoff
## Summary
This document outlines the architecture decision for the Schema-Normalizer to Data-Enricher SNS fan-out handoff in the event-driven microservices pipeline.
## Sequence Diagram
The following sequence diagram illustrates the handoff process:
1. Schema-Normalizer consumes raw SQS messages from the `normalize.ingest` queue.
2. Schema-Normalizer normalizes vendor responses into canonical internal DTOs and publishes the normalized records to the `enrich.pipeline` SNS topic.
3. Data-Enricher subscribes to the `enrich.pipeline` SNS topic and consumes the normalized records.
4. Data-Enricher enriches the normalized records with cross-references, business rules, and derived fields.
5. Data-Enricher persists the enriched records to Cassandra and fans out the results to downstream consumers via SNS.
## Decision Rationale
The decision to use SNS for the handoff between Schema-Normalizer and Data-Enricher was based on the need for a scalable and reliable messaging system that can handle high volumes of data. SNS provides a decoupling layer between the services, allowing them to operate independently and asynchronously.
## Implications
The use of SNS for the handoff between Schema-Normalizer and Data-Enricher has implications for the overall architecture of the pipeline. It requires careful consideration of the SNS topic configuration, subscription management, and error handling mechanisms to ensure reliable and efficient data processing.