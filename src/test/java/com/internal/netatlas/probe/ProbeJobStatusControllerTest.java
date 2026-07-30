package com.internal/netatlas/probe;

import com.internal.netatlas.probe.controller.ProbeJobStatusController;
import com.internal.netatlas.probe.model.ProbeJobStatusDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProbeJobStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetStatus() throws Exception {
        // Mock request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/probe/jobs/123/status")).andExpect(status().isOk());
    }
}