package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.model.EnrichmentMessage;
import com.internal.netatlas.enrich.service.DataEnricherBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQS listener that receives enrichment payloads from the {@code enrich.pipeline} queue.
 * The message is delegated to {@link DataEnricherBatchService} for idempotent processing
 * and visibility‑timeout handling.
 */
@Service
public class DataEnricherSqsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DataEnricherSqsHandler.class);

    private final DataEnricherBatchService batchService;

    @Autowired
    public DataEnricherSqsHandler(DataEnricherBatchService batchService) {
        this.batchService = batchService;
    }

    @SqsListener("enrich.pipeline")
    public void handle(EnrichmentMessage message) {
        LOG.info("Received enrichment message id={}, normalizedRecordId={}",
                message.getMessageId(), message.getNormalizedRecordId());
        batchService.processMessage(message);
    }
}
