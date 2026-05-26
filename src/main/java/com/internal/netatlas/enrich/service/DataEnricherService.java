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

    public EnrichmentResult enrich(String message) {
        // Enrich the message
        return new EnrichmentResult(UUID.randomUUID().toString(), message);
    }

    public boolean isAlreadyProcessed(String idempotencyKey) {
        return dataEnricherRepository.isAlreadyProcessed(idempotencyKey);
    }

    public void markAsProcessed(String idempotencyKey) {
        dataEnricherRepository.markAsProcessed(idempotencyKey);
    }
}