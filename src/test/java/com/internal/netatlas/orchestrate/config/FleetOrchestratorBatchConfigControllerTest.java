package com.internal.netatlas.orchestrate.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class FleetOrchestratorBatchConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUpdateBatchConfig() throws Exception {
        String json = "{\"rapidPollQueueDepthThreshold\": 100}";
        mockMvc.perform(put("/api/v1/orchestrate/config").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }
}