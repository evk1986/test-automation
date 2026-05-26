package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.normalize.model.NormalizedRecord;

import org.springframework.stereotype.Service;

@Service
public class EnrichmentService {
    public EnrichmentResult enrich(NormalizedRecord record) {
        // Perform enrichment logic here
        return new EnrichmentResult(record.getCanonicalInterfaceRecord());
    }
}