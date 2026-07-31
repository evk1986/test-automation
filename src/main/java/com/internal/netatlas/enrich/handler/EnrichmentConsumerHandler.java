package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.EnrichmentConsumerService;
import com.internal.netatlas.enrich.model.EnrichmentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EnrichmentConsumerHandler {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentConsumerHandler.class);
    private final EnrichmentConsumerService service;

    @Autowired
    public EnrichmentConsumerHandler(EnrichmentConsumerService service) {
        this.service = service;
    }

    @SqsListener("enrich.pipeline")
    public void handle(EnrichmentMessage message) {
        logger.info("Received enrichment message id={}", message.getMessageId());
        service.processMessage(message);
    }
}
