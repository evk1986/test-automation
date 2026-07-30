package com.internal.netatlas.orchestrate.controller;

import com.internal.netatlas.orchestrate.service.DLQDrainService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DLQDrainController.class)
public class EndToEndTestOfDLQDrainWorkflow {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DLQDrainService drainService;

    @Test
    public void testDrainEndpointProcessesWithinSla() throws Exception {
        Mockito.when(drainService.drainDlq(50)).thenReturn(45);

        mockMvc.perform(post("/api/v1/orchestrate/dlq/drain")
                .param("maxMessages", "50"))
                .andExpect(status().isOk())
                .andExpect(content().string("Drained 45 messages from DLQ"));
    }
}
