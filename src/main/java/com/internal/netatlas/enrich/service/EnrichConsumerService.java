package com.internal.netatlas.enrich.service;

import org.springframework.stereotype.Service;
import com.internal.netatlas.enrich.model.EnrichMessage;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EnrichConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(EnrichConsumerService.class);
    private final EnrichmentResultRepository repository;

    public EnrichConsumerService(EnrichmentResultRepository repository) {
        this.repository = repository;
    }

    public void process(EnrichMessage message) {
        boolean exists = repository.existsById(message.getMessageId());
        if (exists) {
            logger.info("Duplicate enrichment message ignored id={}", message.getMessageId());
            return;
        }
        EnrichmentResult result = new EnrichmentResult();
        result.setId(message.getMessageId());
        result.setEnrichedFields(message.getPayload());
        result.setEnrichedAt(java.time.Instant.now());
        repository.save(result);
        logger.info("Enrichment result persisted id={}", result.getId());
    }
}
