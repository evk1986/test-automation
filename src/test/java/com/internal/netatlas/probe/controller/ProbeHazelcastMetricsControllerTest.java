package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProbeHazelcastMetricsService;
import com.internal.netatlas.probe.service.ProbeHazelcastMetricsService.ProtocolFailureMetricDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProbeHazelcastMetricsController.class)
class ProbeHazelcastMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProbeHazelcastMetricsService metricsService;

    @Test
    @DisplayName("GET /api/v1/probe/hazelcast/metrics/protocol-failures returns metric DTO")
    void getProtocolFailureMetrics_returnsDto() throws Exception {
        Map<String, String> tags = Collections.singletonMap("protocol", "SNMP");
        ProtocolFailureMetricDto dto = new ProtocolFailureMetricDto("probe.protocol.failures", 3.0, tags);
        Mockito.when(metricsService.getProtocolFailureMetrics()).thenReturn(dto);

        mockMvc.perform(get("/api/v1/probe/hazelcast/metrics/protocol-failures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricName").value("probe.protocol.failures"))
                .andExpect(jsonPath("$.value").value(3.0))
                .andExpect(jsonPath("$.tags.protocol").value("SNMP"));
    }
}
