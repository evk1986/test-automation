package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.internal.netatlas.normalize.model.CanonicalInterface;
import com.internal.netatlas.normalize.model.NormalizedRecord;

public class AristaEosMapper {
    public NormalizedRecord map(JsonNode eosResponse) {
        NormalizedRecord record = new NormalizedRecord();
        record.setCanonicalType("AristaEOS");
        // Add additional mapping logic here
        return record;
    }
}