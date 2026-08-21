package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeLockProbeMetricsRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
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
    private ProbeLockProbeMetricsRepository repository;

    private MeterRegistry meterRegistry;
    private ProbeLockProbeMetricsService service;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        when(hazelcastInstance.getCPSubsystem()).thenReturn(cpSubsystem);
        when(cpSubsystem.getFencedLock(anyString())).thenReturn(fencedLock);
        service = new ProbeLockProbeMetricsService(hazelcastInstance, meterRegistry, repository);
    }

    @Test
    void testExecuteWithLockSuccess() throws InterruptedException {
        ProbeJob job = new ProbeJob("JOB-NETCONF-4821", "DEV-ASR-9001", "NETCONF", "us-east-1", "BATCH-PRB-20240523-USE1-01", "PENDING", 0, null);
        when(fencedLock.tryLock(5, TimeUnit.SECONDS)).thenReturn(true);
        when(fencedLock.isLockedByCurrentThread()).thenReturn(true);

        boolean result = service.executeWithLockAndMetrics(job, () -> {
            log.info("Executing mock task inside lock");
        });

        assertTrue(result);
        assertEquals("SUCCESS", job.getStatus());
        verify(fencedLock).unlock();
        verify(repository, times(2)).save(job);
    }

    @Test
    void testExecuteWithLockFailureIncrementsMetric() throws InterruptedException {
        ProbeJob job = new ProbeJob("JOB-NETCONF-4821", "DEV-ASR-9001", "SNMP", "us-east-1", "BATCH-PRB-20240523-USE1-01", "PENDING", 0, null);
        when(fencedLock.tryLock(5, TimeUnit.SECONDS)).thenReturn(false);

        boolean result = service.executeWithLockAndMetrics(job, () -> {
            fail("Task should not be executed when lock acquisition fails");
        });

        assertFalse(result);
        assertEquals("FAILED", job.getStatus());
        assertEquals(1.0, meterRegistry.get("probe.protocol.failure.count")
                .tag("protocol", "SNMP")
                .tag("reason", "LOCK_ACQUISITION_TIMEOUT")
                .counter().count());
    }
}