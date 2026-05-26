package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.AristaEosMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchemaNormalizerService {
    private final AristaEosMapper aristaEosMapper;

    @Autowired
    public SchemaNormalizerService(AristaEosMapper aristaEosMapper) {
        this.aristaEosMapper = aristaEosMapper;
    }

    public NormalizedRecord normalize(JsonNode eosResponse) {
        return aristaEosMapper.map(eosResponse);
    }
}