package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProbeLockProbeMetricsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProbeLockProbeMetricsController.class)
class ProbeLockProbeMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProbeLockProbeMetricsService probeLockProbeMetricsService;

    @Test
    @DisplayName("GET /api/v1/probe/locks-metrics/{deviceId}/lock-status returns current lock state")
    void shouldReturnLockStatus() throws Exception {
        given(probeLockProbeMetricsService.isDeviceLocked("dev-cisco-asr-01")).willReturn(true);

        mockMvc.perform(get("/api/v1/probe/locks-metrics/dev-cisco-asr-01/lock-status")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deviceId").value("dev-cisco-asr-01"))
            .andExpect(jsonPath("$.locked").value(true))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("POST /api/v1/probe/locks-metrics/{deviceId}/simulate-walk executes walk and returns result")
    void shouldSimulateWalkSuccessfully() throws Exception {
        given(probeLockProbeMetricsService.executeWithLockAndMetric("dev-cisco-asr-01")).willReturn(true);

        mockMvc.perform(post("/api/v1/probe/locks-metrics/dev-cisco-asr-01/simulate-walk")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deviceId").value("dev-cisco-asr-01"))
            .andExpect(jsonPath("$.executedSuccessfully").value(true))
            .andExpect(jsonPath("$.ticket").value("PRB-4821"));
    }
}
