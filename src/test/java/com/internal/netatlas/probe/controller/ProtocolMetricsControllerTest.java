package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProtocolMetricsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProtocolMetricsController.class)
public class ProtocolMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProtocolMetricsService metricsService;

    @Test
    void shouldReturnProtocolFailureMetrics() throws Exception {
        Map<String, Integer> mockMetrics = Map.of(
                "NETCONF", 12,
                "SSH", 8,
                "SNMP", 15,
                "EAPI", 3,
                "GRPC", 0
        );
        Mockito.when(metricsService.fetchFailureMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/v1/probe/metrics/protocol-failure"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"NETCONF\":12,\"SSH\":8,\"SNMP\":15,\"EAPI\":3,\"GRPC\":0}"));
    }
}
