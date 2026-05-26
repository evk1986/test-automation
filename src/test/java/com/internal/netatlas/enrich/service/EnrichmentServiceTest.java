package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class EnrichmentServiceTest {
    @InjectMocks
    private EnrichmentService enrichmentService;

    @Test
    void testEnrich() {
        NormalizedRecord record = new NormalizedRecord();
        EnrichmentResult result = enrichmentService.enrich(record);
        assertNotNull(result);
    }
}