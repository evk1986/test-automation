package com.internal.netatlas.enrich.handler;

import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import com.internal.netatlas.enrich.service.EnrichConsumerService;
import com.internal.netatlas.enrich.model.EnrichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EnrichConsumerHandler {

    private static final Logger logger = LoggerFactory.getLogger(EnrichConsumerHandler.class);
    private final EnrichConsumerService service;

    public EnrichConsumerHandler(EnrichConsumerService service) {
        this.service = service;
    }

    @SqsListener("enrich.pipeline")
    public void handle(EnrichMessage message) {
        logger.info("Received enrichment message id={}", message.getMessageId());
        service.process(message);
    }
}
