package com.internal.netatlas.normalize.mapper;

import com.internal.netatlas.normalize.model.CanonicalInterface;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.InterfaceRecord;
import com.fasterxml.jackson.databind.JsonNode;

public class AristaEosMapper {
    public InterfaceRecord mapToInterfaceRecord(JsonNode jsonNode) {
        InterfaceRecord interfaceRecord = new InterfaceRecord();
        interfaceRecord.setName(jsonNode.get("interfaceName").asText());
        interfaceRecord.setOperationalStatus(jsonNode.has("operationalStatus") ? jsonNode.get("operationalStatus").asText() : "unknown");
        return interfaceRecord;
    }
}