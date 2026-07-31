package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentMessage;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EnrichmentConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentConsumerService.class);
    private final EnrichmentResultRepository repository;

    @Autowired
    public EnrichmentConsumerService(EnrichmentResultRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processMessage(EnrichmentMessage message) {
        // Idempotency check – skip if already persisted
        if (repository.existsById(message.getMessageId())) {
            logger.info("Duplicate enrichment message {} ignored", message.getMessageId());
            return;
        }

        // Simulate visibility timeout extension
        logger.info("Extending visibility timeout for message {}", message.getMessageId());

        EnrichmentResult result = new EnrichmentResult(
                message.getMessageId(),
                message.getNormalizedRecordId(),
                message.getEnrichedFields(),
                System.currentTimeMillis()
        );

        repository.save(result);
        logger.info("Enrichment result persisted for message {}", message.getMessageId());
    }
}
