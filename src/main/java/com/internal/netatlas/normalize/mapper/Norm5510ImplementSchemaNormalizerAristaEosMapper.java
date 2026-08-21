package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.normalize.model.InterfaceRecord;
import org.springframework.stereotype.Component;

@Component
public class Norm5510ImplementSchemaNormalizerAristaEosMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public InterfaceRecord map(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            // Assume the payload contains an "interfaces" array; map the first entry
            JsonNode interfaceNode = root.path("interfaces").elements().next();
            String name = interfaceNode.path("name").asText();
            String description = interfaceNode.path("description").asText("");
            String adminStatus = interfaceNode.path("adminStatus").asText();
            String operStatus = interfaceNode.path("operStatus").asText();
            String macAddress = interfaceNode.path("macAddress").asText();
            int speed = interfaceNode.path("speed").asInt();
            return new InterfaceRecord(name, description, adminStatus, operStatus, macAddress, speed);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to map Arista EOS interface JSON", e);
        }
    }
}
