package com.internal.netatlas.probe.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProbeCommandsDlqMetricCollectorIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void endpointReturnsSuccessStatus() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/probe/jobs/add-dlq-visibility-metrics-and-automated", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void responseBodyContainsTicketIdentifier() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/probe/jobs/add-dlq-visibility-metrics-and-automated", String.class);
        assertThat(response.getBody()).isNotBlank();
    }
}
