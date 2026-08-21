package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.Norm5510ImplementSchemaNormalizerAristaEosMapper;
import com.internal.netatlas.normalize.model.InterfaceRecord;
import org.springframework.stereotype.Service;

@Service
public class Norm5510ImplementSchemaNormalizerService {

    private final Norm5510ImplementSchemaNormalizerAristaEosMapper mapper;

    public Norm5510ImplementSchemaNormalizerService(Norm5510ImplementSchemaNormalizerAristaEosMapper mapper) {
        this.mapper = mapper;
    }

    public InterfaceRecord normalizeAristaEosShowInterfaces(String rawJson) {
        return mapper.map(rawJson);
    }
}
