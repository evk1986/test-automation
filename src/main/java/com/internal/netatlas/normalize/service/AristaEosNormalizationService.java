package com.internal.netatlas.normalize.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class AristaEosNormalizationService {

    private final ObjectMapper mapper = new ObjectMapper();

    public String normalizeInterface(String raw) {
        // Example raw: "Ethernet1,up,1000Mbps"
        String[] parts = raw.split(",");
        ObjectNode node = mapper.createObjectNode();
        if (parts.length >= 3) {
            node.put("interfaceName", parts[0].trim());
            node.put("adminStatus", parts[1].trim());
            node.put("speed", parts[2].trim());
        } else {
            node.put("raw", raw);
        }
        node.put("schemaVersion", "v3");
        try {
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize normalized payload", e);
        }
    }
}
