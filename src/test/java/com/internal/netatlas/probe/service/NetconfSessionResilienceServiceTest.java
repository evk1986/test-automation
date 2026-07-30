package com.internal.netatlas.probe.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NetconfSessionResilienceServiceTest {

    private NetconfSessionResilienceService resilienceService;

    @BeforeEach
    void setUp() {
        resilienceService = new NetconfSessionResilienceService(new SimpleMeterRegistry());
    }

    @Test
    void shouldReturnResultWhenOperationSucceedsFirstAttempt() {
        String result = resilienceService.executeWithResilience(() -> "ok");
        assertEquals("ok", result);
    }

    @Test
    void shouldRetryAndSucceedOnSecondAttempt() {
        final int[] counter = {0};
        String result = resilienceService.executeWithResilience(() -> {
            counter[0]++;
            if (counter[0] < 2) {
                throw new RuntimeException("Transient error");
            }
            return "recovered";
        });
        assertEquals("recovered", result);
        assertEquals(2, counter[0]);
    }

    @Test
    void shouldFailAfterMaxRetries() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                resilienceService.executeWithResilience(() -> {
                    throw new RuntimeException("Permanent error");
                }));
        assertTrue(ex.getMessage().contains("failed after retries"));
    }
}
