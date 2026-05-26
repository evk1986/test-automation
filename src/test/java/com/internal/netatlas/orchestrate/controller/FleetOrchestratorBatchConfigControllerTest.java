package com.internal.netatlas.orchestrate.controller;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FleetOrchestratorBatchConfigControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetBatchConfig() throws Exception {
        mockMvc.perform(get("/api/v1/orchestrator/batch-config"))
                .andExpect(status().isOk());
    }
}