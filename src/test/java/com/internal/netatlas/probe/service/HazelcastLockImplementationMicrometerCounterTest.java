package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HazelcastLockImplementationMicrometerCounterTest {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private CPSubsystem cpSubsystem;

    @Mock
    private FencedLock fencedLock;

    private SimpleMeterRegistry meterRegistry;
    private HazelcastLockImplementationMicrometerCounter service;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        when(hazelcastInstance.getCPSubsystem()).thenReturn(cpSubsystem);
        when(cpSubsystem.getLock(anyString())).thenReturn(fencedLock);
        service = new HazelcastLockImplementationMicrometerCounter(hazelcastInstance, meterRegistry);
    }

    @Test
    void whenLockNotAcquired_thenFailureCounterIncrementedAndExceptionThrown() {
        when(fencedLock.tryLock()).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> service.performSnmpWalk("device-1", "1.3.6.1.2.1"));

        double count = meterRegistry.get("probe.protocol.failure").counter().count();
        assert count == 1.0;
        verify(fencedLock, never()).unlock();
    }

    @Test
    void whenLockAcquired_thenNoFailureIncrementAndUnlockCalled() {
        when(fencedLock.tryLock()).thenReturn(true);
        doNothing().when(fencedLock).unlock();

        service.performSnmpWalk("device-2", "1.3.6.1.2.1");

        double count = meterRegistry.get("probe.protocol.failure").counter().count();
        assert count == 0.0;
        verify(fencedLock, times(1)).unlock();
    }
}
