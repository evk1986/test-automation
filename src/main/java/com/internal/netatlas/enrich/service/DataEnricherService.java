package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import com.amazonaws.services.sqs.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DataEnricherService {
    private final EnrichmentResultRepository enrichmentResultRepository;

    @Autowired
    public DataEnricherService(EnrichmentResultRepository enrichmentResultRepository) {
        this.enrichmentResultRepository = enrichmentResultRepository;
    }

    public boolean isIdempotent(Message message) {
        String messageId = message.getMessageId();
        return enrichmentResultRepository.existsByMessageId(messageId);
    }

    public EnrichmentResult enrich(Message message) {
        String messageId = message.getMessageId();
        EnrichmentResult enrichmentResult = new EnrichmentResult(UUID.randomUUID().toString(), messageId, "enriched data");
        enrichmentResultRepository.save(enrichmentResult);
        return enrichmentResult;
    }
}