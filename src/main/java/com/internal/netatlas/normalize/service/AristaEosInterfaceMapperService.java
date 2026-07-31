package com.internal.netatlas.normalize.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class AristaEosInterfaceMapperService {

    private static final String ENRICH_TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:enrich.pipeline";
    private final SnsClient snsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void mapAndPublish(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode interfaces = root.path("interfaces");
            if (interfaces.isMissingNode() || !interfaces.isObject()) {
                log.warn("No interfaces node found in payload");
                return;
            }
            interfaces.fields().forEachRemaining(entry -> {
                String ifaceName = entry.getKey();
                JsonNode ifaceData = entry.getValue();
                String operStatus = ifaceData.path("operationalStatus").asText("UNKNOWN");
                if (operStatus == null || operStatus.isBlank()) {
                    operStatus = "UNKNOWN";
                }
                ObjectNode canonical = objectMapper.createObjectNode();
                canonical.put("interfaceName", ifaceName);
                canonical.put("operationalStatus", operStatus);
                canonical.put("deviceFamily", "Arista EOS");
                canonical.put("schemaVersion", "1.0");
                PublishRequest request = PublishRequest.builder()
                        .topicArn(ENRICH_TOPIC_ARN)
                        .message(canonical.toString())
                        .build();
                snsClient.publish(request);
                log.info("Published InterfaceRecord for {} to enrichment pipeline", ifaceName);
            });
        } catch (Exception e) {
            log.error("Failed to map Arista EOS payload", e);
        }
    }
}
