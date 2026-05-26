package com.internal.netatlas.orchestrate.controller;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import com.internal.netatlas.orchestrate.service.FleetOrchestratorBatchConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FleetOrchestratorControllerTest {

    @Mock
    private FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService;

    @InjectMocks
    private FleetOrchestratorController fleetOrchestratorController;

    private MockMvc mockMvc;

    @Test
    public void testUpdateBatchConfig() throws Exception {
        BatchConfig batchConfig = new BatchConfig();
        mockMvc = MockMvcBuilders.standaloneSetup(fleetOrchestratorController).build();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orchestrate/batch-config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(batchConfig)))
                .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        // Implement JSON conversion
        return "{}";
    }
}