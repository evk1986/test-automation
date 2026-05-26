package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.AristaEosMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.InterfaceRecord;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;

@Service
public class AristaEosNormalizationService {
    private final AristaEosMapper aristaEosMapper;

    public AristaEosNormalizationService(AristaEosMapper aristaEosMapper) {
        this.aristaEosMapper = aristaEosMapper;
    }

    public NormalizedRecord normalize(JsonNode jsonNode) {
        InterfaceRecord interfaceRecord = aristaEosMapper.mapToInterfaceRecord(jsonNode);
        NormalizedRecord normalizedRecord = new NormalizedRecord();
        normalizedRecord.setInterfaceRecord(interfaceRecord);
        return normalizedRecord;
    }
}