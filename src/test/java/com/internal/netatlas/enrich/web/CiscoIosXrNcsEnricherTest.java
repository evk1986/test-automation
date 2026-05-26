package com.internal.netatlas.enrich.web;

import com.internal.netatlas.enrich.enricher.CiscoIosXrNcsEnricher;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.model.NormalizedRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CiscoIosXrNcsEnricherTest {
    @Autowired
    private CiscoIosXrNcsEnricher enricher;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testEnrichment() throws Exception {
        NormalizedRecord record = new NormalizedRecord();
        EnrichmentResult result = enricher.enrich(record);
        mockMvc.perform(get("/enrichment")).andExpect(status().isOk());
    }
}