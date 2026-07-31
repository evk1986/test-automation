package com.internal.netatlas.probe;

import com.internal.netatlas.probe.service.ProtocolFailureMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QA integration test for PRB-874.
 * Verifies that a protocol failure increments the {@code probe.protocol.failures} counter
 * and that the new value is exposed via the Actuator metrics endpoint.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestSuiteSqsReplay {

    private static final String PROTOCOL = "SNMP";

    @Autowired
    private ProtocolFailureMetricsService metricsService;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void whenProtocolFailureOccurs_metricIsIncremented() {
        double before = meterRegistry.get("probe.protocol.failures")
                .tag("protocol", PROTOCOL)
                .counter()
                .count();

        // Simulate a protocol failure
        metricsService.recordFailure(PROTOCOL);

        double after = meterRegistry.get("probe.protocol.failures")
                .tag("protocol", PROTOCOL)
                .counter()
                .count();

        assertEquals(before + 1.0, after, 0.0001, "Counter should increase by one after failure");

        // Verify the value through the Actuator endpoint
        ResponseEntity<MetricsEndpoint.MetricResponse> response = restTemplate.getForEntity(
                "/actuator/metrics/probe.protocol.failures?tag=protocol:" + PROTOCOL,
                MetricsEndpoint.MetricResponse.class);

        assertEquals(200, response.getStatusCodeValue(), "Actuator endpoint should return HTTP 200");
        assertNotNull(response.getBody(), "Metric response body must not be null");
        assertTrue(response.getBody().measurements().stream()
                .anyMatch(m -> Math.abs(m.getValue() - after) < 0.0001),
                "Actuator metric value should match the updated counter");
    }
}
