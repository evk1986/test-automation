package com.internal.netatlas.probe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HardenFleetOrchestratorBatchConfigAController.class)
class HardenFleetOrchestratorBatchConfigAControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private HardenFleetOrchestratorBatchConfigAService service;

    @Test
    void returnsOk() throws Exception {
        when(service.execute()).thenReturn("ok");
        mockMvc.perform(get("/api/v1/probe/jobs/harden-fleet-orchestrator-batch-config-a")).andExpect(status().isOk());
    }
}
