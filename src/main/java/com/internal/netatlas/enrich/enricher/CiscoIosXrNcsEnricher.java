package com.internal.netatlas.enrich.enricher;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.model.NormalizedRecord;
import org.springframework.stereotype.Service;

@Service
public class CiscoIosXrNcsEnricher {
    public EnrichmentResult enrich(NormalizedRecord record) {
        // Enrich the record with Cisco IOS-XR NCS specific data
        EnrichmentResult result = new EnrichmentResult();
        result.setEnrichedFields(record.getNormalizedPayload());
        return result;
    }
}