package com.internal.netatlas.probe.handler;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.internal.netatlas.probe.service.NetconfProbeProcessingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProbeHandlersNetconfProbeLockingTest {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ILock lock;

    @Mock
    private ProbeJobRepository probeJobRepository;

    @Mock
    private NetconfProbeProcessingService processingService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter lockContentionCounter;

    @Mock
    private Counter idempotentSkipCounter;

    @InjectMocks
    private ProbeHandlersNetconfProbeLocking handler;

    private ProbeJobMessage sampleMessage;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter("netconf.probe.lock.contention")).thenReturn(lockContentionCounter);
        when(meterRegistry.counter("netconf.probe.idempotent.skip")).thenReturn(idempotentSkipCounter);
        when(hazelcastInstance.getLock(anyString())).thenReturn(lock);
        sampleMessage = new ProbeJobMessage();
        sampleMessage.setJobId("JOB-NETCONF-4821");
        sampleMessage.setDeviceId("device-123");
        sampleMessage.setProtocol("NETCONF");
    }

    @Test
    void whenLockCannotBeAcquired_thenContentionCounterIncremented() throws Exception {
        when(lock.tryLock(anyLong(), any())).thenReturn(false);

        handler.handle(sampleMessage);

        verify(lockContentionCounter, times(1)).increment();
        verifyNoInteractions(processingService);
        verifyNoInteractions(idempotentSkipCounter);
    }

    @Test
    void whenJobAlreadySuccessful_thenIdempotentSkipCounterIncremented() throws Exception {
        when(lock.tryLock(anyLong(), any())).thenReturn(true);
        // Mock a job entity with SUCCESS status
        var mockJob = mock(com.internal.netatlas.probe.model.ProbeJob.class);
        when(mockJob.getStatus()).thenReturn(com.internal.netatlas.probe.model.ProbeJob.Status.SUCCESS);
        when(probeJobRepository.findById(sampleMessage.getJobId())).thenReturn(Optional.of(mockJob));

        handler.handle(sampleMessage);

        verify(idempotentSkipCounter, times(1)).increment();
        verify(processingService, never()).process(any());
        verify(lock, times(1)).unlock();
    }

    @Test
    void whenLockAcquiredAndJobNotProcessed_thenProcessingServiceInvoked() throws Exception {
        when(lock.tryLock(anyLong(), any())).thenReturn(true);
        when(probeJobRepository.findById(sampleMessage.getJobId())).thenReturn(Optional.empty());

        handler.handle(sampleMessage);

        verify(processingService, times(1)).process(sampleMessage);
        verify(lock, times(1)).unlock();
        verifyNoInteractions(idempotentSkipCounter);
        verifyNoInteractions(lockContentionCounter);
    }
}
