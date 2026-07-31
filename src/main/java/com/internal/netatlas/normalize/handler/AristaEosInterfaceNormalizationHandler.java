package com.internal.netatlas.normalize.handler;

import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import com.internal.netatlas.normalize.service.AristaEosNormalizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AristaEosInterfaceNormalizationHandler {

    private static final Logger logger = LoggerFactory.getLogger(AristaEosInterfaceNormalizationHandler.class);
    private final AristaEosNormalizationService normalizationService;

    public AristaEosInterfaceNormalizationHandler(AristaEosNormalizationService normalizationService) {
        this.normalizationService = normalizationService;
    }

    @SqsListener("normalize.ingest")
    public void handle(String rawMessage) {
        logger.info("Received raw EOS interface payload");
        String normalized = normalizationService.normalizeInterface(rawMessage);
        logger.info("Normalized payload: {}", normalized);
        // In real flow, publish to SNS topic; omitted for brevity
    }
}
