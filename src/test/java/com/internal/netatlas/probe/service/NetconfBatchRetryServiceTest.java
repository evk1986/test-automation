package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link NetconfBatchRetryService} focusing on Micrometer failure metric recording.
 */
class NetconfBatchRetryServiceTest {

    private ProbeJobRepository probeJobRepository;
    private SimpleMeterRegistry meterRegistry;
    private NetconfBatchRetryService retryService;

    @BeforeEach
    void setUp() {
        probeJobRepository = Mockito.mock(ProbeJobRepository.class);
        meterRegistry = new SimpleMeterRegistry();
        retryService = new NetconfBatchRetryService(probeJobRepository, meterRegistry);
    }

    @Test
    void shouldIncrementNetconfFailureCounterWhenRetryFails() {
        // Arrange: a failed job with region us-east-1 and an odd attemptCount to trigger failure.
        ProbeJob failedJob = new ProbeJob();
        failedJob.setId("JOB-NETCONF-4821");
        failedJob.setBatchId("BATCH-PRB-20240523-USE1-01");
        failedJob.setRegion("us-east-1");
        failedJob.setProtocol(ProbeJob.Protocol.NETCONF);
        failedJob.setStatus(ProbeJob.Status.FAILED);
        failedJob.setAttemptCount(1); // odd → simulated failure

        Mockito.when(probeJobRepository.findByBatchIdAndStatus("BATCH-PRB-20240523-USE1-01", ProbeJob.Status.FAILED))
                .thenReturn(Collections.singletonList(failedJob));
        Mockito.when(probeJobRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        retryService.retryFailedJobs("BATCH-PRB-20240523-USE1-01");

        // Assert: counter should have been incremented once with the correct tags.
        double count = meterRegistry.get("probe.protocol.failures")
                .tag("protocol", "NETCONF")
                .tag("region", "us-east-1")
                .counter()
                .count();
        assertEquals(1.0, count, "NETCONF failure counter should be incremented exactly once");
    }
}
