package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.repository.DataEnricherRepository;
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

    public boolean isDuplicate(String idempotencyKey) {
        return dataEnricherRepository.findByIdempotencyKey(idempotencyKey).isPresent();
    }

    public EnrichmentResult enrich(String messageBody) {
        // enrichment logic here
        return new EnrichmentResult(UUID.randomUUID().toString(), messageBody);
    }
}