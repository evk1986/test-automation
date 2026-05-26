package com.internal.netatlas.enrich.repository;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import org.springframework.data.repository.CrudRepository;

public interface EnrichmentResultRepository extends CrudRepository<EnrichmentResult, String> {
}