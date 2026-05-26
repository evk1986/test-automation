package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DataEnricherService {

    private final DataEnricherRepository dataEnricherRepository;

    @Autowired
    public DataEnricherService(DataEnricherRepository dataEnricherRepository) {
        this.dataEnricherRepository = dataEnricherRepository;
    }

    public EnrichmentResult enrich(String message, String idempotencyKey) {
        // Implement enrichment logic here
        EnrichmentResult enrichmentResult = new EnrichmentResult(UUID.randomUUID().toString(), message, idempotencyKey);
        dataEnricherRepository.save(enrichmentResult);
        return enrichmentResult;
    }
}