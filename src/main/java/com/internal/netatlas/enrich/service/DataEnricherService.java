package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;

public interface DataEnricherService {

    EnrichmentResult processMessage(com.amazonaws.services.sqs.model.Message message);
}

class DataEnricherServiceImpl implements DataEnricherService {

    @Override
    public EnrichmentResult processMessage(com.amazonaws.services.sqs.model.Message message) {
        // Implement the enrichment logic here
        return new EnrichmentResult();
    }
}