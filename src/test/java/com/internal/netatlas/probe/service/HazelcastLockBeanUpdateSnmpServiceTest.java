package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HazelcastLockBeanUpdateSnmpServiceTest {

    @Mock
    private HazelcastInstance hazelcastInstance;
    @Mock
    private ILock lock;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter acquiredCounter;
    @Mock
    private Counter releasedCounter;

    private HazelcastLockBeanUpdateSnmpService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(meterRegistry.counter("snmp.lock.acquired")).thenReturn(acquiredCounter);
        when(meterRegistry.counter("snmp.lock.released")).thenReturn(releasedCounter);
        when(hazelcastInstance.getLock(anyString())).thenReturn(lock);
        service = new HazelcastLockBeanUpdateSnmpService(hazelcastInstance, meterRegistry);
    }

    @Test
    void shouldAcquireLockAndReleaseWhenAvailable() throws Exception {
        when(lock.tryLock(anyLong(), eq(TimeUnit.SECONDS))).thenReturn(true);
        when(lock.isLockedByCurrentThread()).thenReturn(true);

        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setDeviceId("device-123");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");

        service.executeSnmpWalk(msg);

        verify(lock).tryLock(eq(5L), eq(TimeUnit.SECONDS));
        verify(acquiredCounter).increment();
        verify(lock).unlock();
        verify(releasedCounter).increment();
    }

    @Test
    void shouldSkipExecutionWhenLockNotAcquired() throws Exception {
        when(lock.tryLock(anyLong(), eq(TimeUnit.SECONDS))).thenReturn(false);

        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setDeviceId("device-456");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");

        service.executeSnmpWalk(msg);

        verify(lock).tryLock(eq(5L), eq(TimeUnit.SECONDS));
        verifyNoInteractions(acquiredCounter);
        verify(lock, never()).unlock();
        verifyNoInteractions(releasedCounter);
    }

    @Test
    void lockKeyShouldFollowSchema() throws Exception {
        when(lock.tryLock(anyLong(), eq(TimeUnit.SECONDS))).thenReturn(true);
        when(lock.isLockedByCurrentThread()).thenReturn(true);

        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setDeviceId("cisco-01");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");

        service.executeSnmpWalk(msg);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(hazelcastInstance).getLock(keyCaptor.capture());
        assertEquals("cisco-01|BATCH-PRB-20240523-USE1-01", keyCaptor.getValue());
    }
}
