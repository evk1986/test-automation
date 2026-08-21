package com.internal.netatlas.normalize.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class Norm5510InterfaceNormalizerService {

    private static final Logger log = LoggerFactory.getLogger(Norm5510InterfaceNormalizerService.class);
    private static final String SCHEMA_VERSION = "v3";

    public ObjectNode processAndNormalize(String snapshotId, String deviceFamily, JsonNode rawPayload) {
        log.info("Normalizing record snapshotId={} for deviceFamily={} with schema {}", snapshotId, deviceFamily, SCHEMA_VERSION);
        
        ObjectNode record = JsonNodeFactory.instance.objectNode();
        record.put("snapshotId", snapshotId);
        record.put("deviceFamily", deviceFamily);
        record.put("schemaVersion", SCHEMA_VERSION);
        record.put("normalizedAt", Instant.now().toString());

        ObjectNode interfacesNode = record.putObject("interfaces");

        if ("Arista EOS".equalsIgnoreCase(deviceFamily) || "ARISTA_EOS".equalsIgnoreCase(deviceFamily)) {
            parseAristaEapiInterfaces(rawPayload, interfacesNode);
        } else {
            parseGenericInterfaces(rawPayload, interfacesNode);
        }

        return record;
    }

    private void parseAristaEapiInterfaces(JsonNode rawPayload, ObjectNode targetNode) {
        JsonNode interfaces = rawPayload.path("result").path(0).path("interfaces");
        if (interfaces.isMissingNode() || !interfaces.isObject()) {
            interfaces = rawPayload.path("interfaces");
        }

        if (interfaces.isObject()) {
            interfaces.fields().forEachRemaining(entry -> {
                String interfaceName = entry.getKey();
                JsonNode details = entry.getValue();

                ObjectNode ifaceData = targetNode.putObject(interfaceName);
                ifaceData.put("name", interfaceName);
                ifaceData.put("description", details.path("description").asText(""));
                ifaceData.put("mtu", details.path("mtu").asInt(1500));
                ifaceData.put("macAddress", details.path("physicalAddress").asText("N/A"));

                String opStatus = details.has("lineProtocolStatus") 
                        ? details.get("lineProtocolStatus").asText("unknown") 
                        : (details.has("interfaceStatus") ? details.get("interfaceStatus").asText("unknown") : "unknown");
                ifaceData.put("operStatus", opStatus);

                String adminStatus = details.has("interfaceStatus") 
                        ? details.get("interfaceStatus").asText("unknown") 
                        : "unknown";
                ifaceData.put("adminStatus", adminStatus);
            });
        }
    }

    private void parseGenericInterfaces(JsonNode rawPayload, ObjectNode targetNode) {
        JsonNode interfaces = rawPayload.path("interfaces");
        if (interfaces.isArray()) {
            for (JsonNode item : interfaces) {
                String name = item.path("name").asText("unknown");
                ObjectNode ifaceData = targetNode.putObject(name);
                ifaceData.put("name", name);
                ifaceData.put("operStatus", item.path("operStatus").asText("unknown"));
                ifaceData.put("adminStatus", item.path("adminStatus").asText("unknown"));
            }
        }
    }
}
