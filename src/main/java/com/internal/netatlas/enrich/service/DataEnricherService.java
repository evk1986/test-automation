package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.model.NormalizedRecord;
import org.springframework.stereotype.Service;

@Service
public class DataEnricherService {
    public EnrichmentResult enrich(NormalizedRecord record) {
        // Implement enrichment logic for v3 contract
        EnrichmentResult result = new EnrichmentResult();
        result.setEnrichedFields(record.getNormalizedPayload());
        return result;
    }
}