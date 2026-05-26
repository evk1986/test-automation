package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.internal.netatlas.normalize.model.CanonicalInterface;
import com.internal.netatlas.normalize.model.NormalizedRecord;

public class AristaEosEapiMapper {

    public NormalizedRecord mapToCanonicalInterface(JsonNode eapiResponse) {
        CanonicalInterface interfaceRecord = new CanonicalInterface();
        interfaceRecord.setInterfaceName(eapiResponse.get("interfaceName").asText());
        interfaceRecord.setOperationalStatus(eapiResponse.has("operationalStatus") ? eapiResponse.get("operationalStatus").asText() : "Unknown");
        return new NormalizedRecord(interfaceRecord);
    }
}