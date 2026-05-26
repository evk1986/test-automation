package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.internal.netatlas.normalize.model.CanonicalInterface;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.stereotype.Component;

@Component
public class CiscoIosXrNetconfMapper {
    public NormalizedRecord map(JsonNode netconfResponse) {
        NormalizedRecord normalizedRecord = new NormalizedRecord();
        normalizedRecord.setCanonicalType("Cisco IOS-XR");
        normalizedRecord.setNormalizedPayload(netconfResponse);
        return normalizedRecord;
    }
}