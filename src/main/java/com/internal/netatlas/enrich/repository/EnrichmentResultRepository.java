package com.internal.netatlas.enrich.repository;

import com.internal.netatlas.enrich.service.DocsRunbooksDataEnricherIdempotencyService.EnrichmentResult;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Cassandra repository for idempotency records created by the Data‑Enricher.
 */
@Repository
public interface EnrichmentResultRepository extends CassandraRepository<EnrichmentResult, String> {
    // Spring Data provides CRUD methods; no custom query required for idempotency check.
}
