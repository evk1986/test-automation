package com.internal.netatlas.probe;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImplementAristaEosMapperInSchemaNoService {
    private static final Logger log = LoggerFactory.getLogger(ImplementAristaEosMapperInSchemaNoService.class);

    public String execute() {
        log.info("Implement Arista EOS mapper in Schema-Normalizer — processing");
        // ## Description Implement a mapper for Arista EOS devices in the Schema-Normalizer service. ## Scope - Schema-Normalizer 
        return "TES-63: processing complete";
    }
}
