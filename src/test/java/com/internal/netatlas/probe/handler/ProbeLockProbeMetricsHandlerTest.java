package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.service.ProbeLockProbeMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProbeLockProbeMetricsHandlerTest {

    @Mock
    private ProbeLockProbeMetricsService probeLockProbeMetricsService;

    @InjectMocks
    private ProbeLockProbeMetricsHandler probeLockProbeMetricsHandler;

    @Test
    void shouldHandleMessageAndDelegateToService() {
        when(probeLockProbeMetricsService.executeWithLock(
                eq("BATCH-PRB-20240523-USE1-01"),
                eq("DEV-ASR-9001"),
                eq("SNMP"),
                eq("prod-use1"),
                any(Runnable.class)
        )).thenReturn(true);

        probeLockProbeMetricsHandler.handle("sample-payload");

        verify(probeLockProbeMetricsService).executeWithLock(
                eq("BATCH-PRB-20240523-USE1-01"),
                eq("DEV-ASR-9001"),
                eq("SNMP"),
                eq("prod-use1"),
                any(Runnable.class)
        );
    }
}
