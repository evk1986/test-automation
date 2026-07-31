package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.AristaEosEnrichmentService;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AristaEosEnrichmentHandlerTest {

    @Mock
    private AristaEosEnrichmentService enrichmentService;

    @InjectMocks
    private AristaEosEnrichmentHandler handler;

    @Test
    void shouldDelegateAristaEosRecordToService() {
        NormalizedRecord record = new NormalizedRecord();
        record.setId("rec-123");
        record.setCanonicalType("AristaEos");

        handler.handle(record);

        verify(enrichmentService).enrich(record);
    }

    @Test
    void shouldIgnoreNonAristaRecord() {
        NormalizedRecord record = new NormalizedRecord();
        record.setId("rec-456");
        record.setCanonicalType("CiscoIosXe");

        handler.handle(record);

        verifyNoInteractions(enrichmentService);
    }
}
