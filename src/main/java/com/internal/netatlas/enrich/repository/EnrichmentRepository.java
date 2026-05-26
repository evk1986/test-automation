package com.internal.netatlas.enrich.repository;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.normalize.model.NormalizedRecord;

import org.springframework.data.repository.CrudRepository;

public interface EnrichmentRepository extends CrudRepository<EnrichmentResult, String> {
    void save(EnrichmentResult result);
}