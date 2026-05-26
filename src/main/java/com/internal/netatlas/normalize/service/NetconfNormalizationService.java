package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.CiscoIosXrNetconfMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetconfNormalizationService {
    private final CiscoIosXrNetconfMapper ciscoIosXrNetconfMapper;

    @Autowired
    public NetconfNormalizationService(CiscoIosXrNetconfMapper ciscoIosXrNetconfMapper) {
        this.ciscoIosXrNetconfMapper = ciscoIosXrNetconfMapper;
    }

    public NormalizedRecord normalize(JsonNode netconfResponse) {
        return ciscoIosXrNetconfMapper.map(netconfResponse);
    }
}