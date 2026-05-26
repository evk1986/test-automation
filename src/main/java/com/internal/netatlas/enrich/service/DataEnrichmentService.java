package com/internal/netatlas/enrich/service;

import com.internal.netatlas.enrich.enricher.CiscoIosXrNcsEnricher;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.model.NormalizedRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataEnrichmentService {
    private final CiscoIosXrNcsEnricher enricher;

    @Autowired
    public DataEnrichmentService(CiscoIosXrNcsEnricher enricher) {
        this.enricher = enricher;
    }

    public EnrichmentResult enrichData(NormalizedRecord record) {
        return enricher.enrich(record);
    }
}