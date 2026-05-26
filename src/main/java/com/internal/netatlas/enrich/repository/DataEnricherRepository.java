package com.internal.netatlas.enrich.repository;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DataEnricherRepository extends CrudRepository<EnrichmentResult, UUID> {

    boolean isAlreadyProcessed(String idempotencyKey);

    void markAsProcessed(String idempotencyKey);
}