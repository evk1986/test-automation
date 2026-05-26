package com.internal.netatlas.enrich.controller;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.model.NormalizedRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataEnricherController.class)
public class DataEnricherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataEnricherService enricherService;

    @Test
    public void testEnrich() throws Exception {
        // Implement test for enrichment API endpoint
        mockMvc.perform(get("/api/v1/enrich/1/enrich").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}