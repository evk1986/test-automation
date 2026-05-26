package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.AristaEosEapiMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.JsonNode;

public class SchemaNormalizerService {
    private final AristaEosEapiMapper aristaEosEapiMapper;

    public SchemaNormalizerService(AristaEosEapiMapper aristaEosEapiMapper) {
        this.aristaEosEapiMapper = aristaEosEapiMapper;
    }

    public NormalizedRecord normalize(JsonNode eapiResponse) {
        return aristaEosEapiMapper.map(eapiResponse);
    }
}