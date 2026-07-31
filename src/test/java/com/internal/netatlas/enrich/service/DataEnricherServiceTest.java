package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DataEnricherService#enrichWithIdempotency(String, String)}.
 * Verifies that a duplicate idempotency key does not trigger the heavy enrichment path.
 */
class DataEnricherServiceTest {

    private DataEnricherService enricherService;
    private DataEnricherService spyService;

    @BeforeEach
    void setUp() {
        enricherService = new DataEnricherService();
        // Spy allows us to verify calls to the internal actualEnrich method.
        spyService = Mockito.spy(enricherService);
    }

    @Test
    void enrichWithIdempotency_firstCall_performsEnrichment() {
        String recordId = "rec-123";
        String key = "uniq-key-1";

        EnrichmentResult result = spyService.enrichWithIdempotency(recordId, key);

        assertNotNull(result);
        assertEquals("enr-" + recordId, result.getId());
        // Verify that the heavy path was executed exactly once.
        verify(spyService, times(1)).actualEnrich(recordId);
    }

    @Test
    void enrichWithIdempotency_duplicateKey_skipsEnrichment() {
        String recordId = "rec-456";
        String key = "dup-key-99";

        // First call – should invoke actualEnrich.
        EnrichmentResult first = spyService.enrichWithIdempotency(recordId, key);
        assertNotNull(first);
        verify(spyService, times(1)).actualEnrich(recordId);

        // Reset interactions to focus on the second call.
        clearInvocations(spyService);

        // Second call with same key – should NOT invoke actualEnrich.
        EnrichmentResult second = spyService.enrichWithIdempotency(recordId, key);
        assertNotNull(second);
        assertEquals("duplicate-" + recordId, second.getId());
        verify(spyService, never()).actualEnrich(anyString());
    }
}
