package com.internal.netatlas.probe.service;

import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.core.HazelcastInstance;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class HazelcastLockImplementationMicrometerCounterTest {

    private HazelcastInstance hazelcastInstance;
    private FencedLock fencedLock;
    private HazelcastLockImplementationMicrometerCounter lockService;
    private SimpleMeterRegistry meterRegistry;

    @BeforeEach
    public void setUp() {
        hazelcastInstance = mock(HazelcastInstance.class);
        fencedLock = mock(FencedLock.class);
        when(hazelcastInstance.getCPSubsystem()).thenReturn(mock(com.hazelcast.cp.CPSubsystem.class));
        when(hazelcastInstance.getCPSubsystem().getLock(anyString())).thenReturn(fencedLock);
        meterRegistry = new SimpleMeterRegistry();
        lockService = new HazelcastLockImplementationMicrometerCounter(hazelcastInstance, meterRegistry);
    }

    @Test
    public void testAcquireAndReleaseLock() throws Exception {
        when(fencedLock.tryLock(anyLong(), any())).thenReturn(true);
        when(fencedLock.isLockedByCurrentThread()).thenReturn(true);

        boolean acquired = lockService.acquireLock("device-123", "BATCH-PRB-20240523-USE1-01");
        assertTrue(acquired, "Lock should be acquired");

        lockService.releaseLock("device-123", "BATCH-PRB-20240523-USE1-01");
        verify(fencedLock, times(1)).unlock();
    }

    @Test
    public void testLockContention() throws Exception {
        // First call acquires the lock, second call fails because lock is already held.
        when(fencedLock.tryLock(anyLong(), any()))
                .thenReturn(true)   // first attempt
                .thenReturn(false); // second attempt
        when(fencedLock.isLockedByCurrentThread()).thenReturn(true);

        assertTrue(lockService.acquireLock("device-456", "BATCH-PRB-20240523-USE1-01"));
        // Simulate another worker trying to acquire the same lock
        assertFalse(lockService.acquireLock("device-456", "BATCH-PRB-20240523-USE1-01"), "Second acquisition should fail due to contention");
    }

    @Test
    public void testFailureMetricIncrement() {
        lockService.recordFailure("SNMP", "us-east-1");
        lockService.recordFailure("SNMP", "us-east-1");
        double count = meterRegistry.get("probe.protocol.failures").tags("protocol", "SNMP", "region", "us-east-1").counter().count();
        assertEquals(2.0, count, "Failure counter should reflect two increments");
    }
}
