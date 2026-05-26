package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.internal.netatlas.normalize.model.CanonicalInterface;
import com.internal.netatlas.normalize.model.NormalizedRecord;

public class AristaEosEapiMapper {
    public NormalizedRecord map(JsonNode eapiResponse) {
        NormalizedRecord record = new NormalizedRecord();
        record.setCanonicalInterface(mapInterface(eapiResponse));
        return record;
    }

    private CanonicalInterface mapInterface(JsonNode eapiResponse) {
        CanonicalInterface interfaceRecord = new CanonicalInterface();
        interfaceRecord.setName(eapiResponse.get("interfaceName").asText());
        interfaceRecord.setOperationalStatus(eapiResponse.has("operationalStatus") ? eapiResponse.get("operationalStatus").asText() : "UNKNOWN");
        return interfaceRecord;
    }
}