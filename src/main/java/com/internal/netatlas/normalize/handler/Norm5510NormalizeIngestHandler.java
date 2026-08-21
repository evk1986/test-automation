package com.internal.netatlas.normalize.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.normalize.service.Norm5510InterfaceNormalizerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class Norm5510NormalizeIngestHandler {

    private static final Logger log = LoggerFactory.getLogger(Norm5510NormalizeIngestHandler.class);

    private final Norm5510InterfaceNormalizerService normalizerService;
    private final ObjectMapper objectMapper;

    public Norm5510NormalizeIngestHandler(Norm5510InterfaceNormalizerService normalizerService, ObjectMapper objectMapper) {
        this.normalizerService = normalizerService;
        this.objectMapper = objectMapper;
    }

    @SqsListener("normalize.ingest")
    public void handle(String rawMessage) {
        try {
            JsonNode payloadNode = objectMapper.readTree(rawMessage);
            String deviceFamily = payloadNode.has("deviceFamily") ? payloadNode.get("deviceFamily").asText() : "UNKNOWN";
            String snapshotId = payloadNode.has("snapshotId") ? payloadNode.get("snapshotId").asText() : "UNKNOWN";
            
            log.info("Received raw ingest payload for snapshotId={} deviceFamily={}", snapshotId, deviceFamily);
            normalizerService.processAndNormalize(snapshotId, deviceFamily, payloadNode);
        } catch (Exception e) {
            log.error("Failed to process message from normalize.ingest queue", e);
            throw new RuntimeException("Normalization processing error", e);
        }
    }
}
