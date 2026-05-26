package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.CiscoIosXrNetconfMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchemaNormalizerService {
    private final CiscoIosXrNetconfMapper mapper;

    @Autowired
    public SchemaNormalizerService(CiscoIosXrNetconfMapper mapper) {
        this.mapper = mapper;
    }

    public NormalizedRecord normalize(JSONObject netconfResponse) {
        return mapper.map(netconfResponse);
    }
}