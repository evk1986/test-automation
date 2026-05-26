package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.AristaEosEapiMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.JsonNode;

public class NormalizationService {

    private final AristaEosEapiMapper aristaEosEapiMapper;

    public NormalizationService(AristaEosEapiMapper aristaEosEapiMapper) {
        this.aristaEosEapiMapper = aristaEosEapiMapper;
    }

    public NormalizedRecord normalizeEapiResponse(JsonNode eapiResponse) {
        return aristaEosEapiMapper.mapToCanonicalInterface(eapiResponse);
    }
}