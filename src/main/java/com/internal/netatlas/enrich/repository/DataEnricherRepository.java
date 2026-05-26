package com.internal.netatlas.enrich.repository;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataEnricherRepository extends CrudRepository<EnrichmentResult, String> {
}