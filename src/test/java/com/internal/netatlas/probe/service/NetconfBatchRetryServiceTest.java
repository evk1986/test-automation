package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NetconfBatchRetryServiceTest {

    @Mock
    private ProbeJobRepository probeJobRepository;

    @InjectMocks
    private NetconfBatchRetryService netconfBatchRetryService;

    @Test
    void testRetryFailedJobs() {
        // Given
        ProbeJob job = new ProbeJob();
        job.setBatchId("BATCH-PRB-20240523-USE1-01");
        job.setStatus("FAILED");
        when(probeJobRepository.findByBatchIdAndStatus("BATCH-PRB-20240523-USE1-01", "FAILED"))
                .thenReturn(List.of(job));

        // When
        netconfBatchRetryService.retryFailedJobs("BATCH-PRB-20240523-USE1-01");

        // Then
        // Verify that the job status is updated to PENDING
    }
}