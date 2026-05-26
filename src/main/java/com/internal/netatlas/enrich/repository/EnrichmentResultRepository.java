package com.internal.netatlas.enrich.repository;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrichmentResultRepository extends CrudRepository<EnrichmentResult, String> {
    boolean existsByMessageId(String messageId);
}