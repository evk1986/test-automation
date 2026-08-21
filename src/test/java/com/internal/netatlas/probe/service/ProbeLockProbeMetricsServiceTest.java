package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProbeLockProbeMetricsServiceTest {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private CPSubsystem cpSubsystem;

    @Mock
    private FencedLock fencedLock;

    @Mock
    private ProbeJobRepository probeJobRepository;

    private MeterRegistry meterRegistry;
    private ProbeLockProbeMetricsService service;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        when(hazelcastInstance.getCPSubsystem()).thenReturn(cpSubsystem);
        when(cpSubsystem.getLock(any())).thenReturn(fencedLock);

        service = new ProbeLockProbeMetricsService(hazelcastInstance, meterRegistry, probeJobRepository);
    }

    @Test
    void testProcessProbeWithLock_Success() throws InterruptedException {
        when(fencedLock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(fencedLock.isLockedByCurrentThread()).thenReturn(true);

        ProbeJob job = new ProbeJob();
        job.setId("JOB-NETCONF-4821");
        job.setDeviceId("router-cisco-01");
        job.setProtocol("NETCONF");
        job.setBatchId("BATCH-PRB-20240523-USE1-01");
        job.setAttemptCount(0);

        boolean result = service.processProbeWithLock(job);

        assertTrue(result);
        verify(probeJobRepository).save(job);
        verify(fencedLock).unlock();

        Counter counter = meterRegistry.find("probe.protocol.execution.success").counter();
        assertTrue(counter != null && counter.count() == 1.0);
    }

    @Test
    void testProcessProbeWithLock_LockAcquisitionFailure() throws InterruptedException {
        when(fencedLock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(false);

        ProbeJob job = new ProbeJob();
        job.setId("JOB-NETCONF-4821");
        job.setDeviceId("router-cisco-01");
        job.setProtocol("NETCONF");
        job.setBatchId("BATCH-PRB-20240523-USE1-01");

        boolean result = service.processProbeWithLock(job);

        assertFalse(result);

        Counter failureCounter = meterRegistry.find("probe.lock.acquisition.failure").counter();
        assertTrue(failureCounter != null && failureCounter.count() == 1.0);
    }

    @Test
    void testProcessProbeWithLock_ExecutionFailureIncrementsFailureMetric() throws InterruptedException {
        when(fencedLock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(fencedLock.isLockedByCurrentThread()).thenReturn(true);

        ProbeJob job = new ProbeJob();
        job.setId("JOB-NETCONF-4822");
        job.setDeviceId("OFFLINE");
        job.setProtocol("SNMP");
        job.setBatchId("POLL-RAPID-77402");

        boolean result = service.processProbeWithLock(job);

        assertFalse(result);
        verify(fencedLock).unlock();

        Counter failureCounter = meterRegistry.find("probe.protocol.execution.failure").counter();
        assertTrue(failureCounter != null && failureCounter.count() == 1.0);
    }
}
