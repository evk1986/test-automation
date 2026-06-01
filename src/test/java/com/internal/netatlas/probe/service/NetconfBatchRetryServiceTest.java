package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NetconfBatchRetryServiceTest {

    @Mock
    private ProbeJobRepository probeJobRepository;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @InjectMocks
    private NetconfBatchRetryService netconfBatchRetryService;

    @Test
    void testRetryFailedJobs() {
        // given
        ProbeJob job1 = new ProbeJob();
        job1.setBatchId("BATCH-PRB-20240523-USE1-01");
        job1.setStatus("FAILED");
        ProbeJob job2 = new ProbeJob();
        job2.setBatchId("BATCH-PRB-20240523-USE1-01");
        job2.setStatus("FAILED");
        when(probeJobRepository.findByBatchIdAndStatus(any(), any())).thenReturn(Arrays.asList(job1, job2));
        ILock lock = org.mockito.Mockito.mock(ILock.class);
        when(hazelcastInstance.getLock(any())).thenReturn(lock);

        // when
        netconfBatchRetryService.retryFailedJobs("BATCH-PRB-20240523-USE1-01");

        // then
        verify(probeJobRepository, org.mockito.Mockito.times(1)).save(job1);
        verify(probeJobRepository, org.mockito.Mockito.times(1)).save(job2);
        verify(lock, org.mockito.Mockito.times(1)).lock();
        verify(lock, org.mockito.Mockito.times(1)).unlock();
    }
}