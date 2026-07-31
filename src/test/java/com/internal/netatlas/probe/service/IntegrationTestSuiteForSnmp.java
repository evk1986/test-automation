package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.repository.DeviceSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IntegrationTestSuiteForSnmp {

    @Mock
    private HazelcastInstance hazelcastInstance;
    @Mock
    private IMap<String, String> lockMap;
    @Mock
    private DeviceSnapshotRepository snapshotRepository;

    private SnmpWalkLockService snmpWalkLockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(hazelcastInstance.getMap("snmp-walk-locks")).thenReturn(lockMap);
        snmpWalkLockService = new SnmpWalkLockService(hazelcastInstance, snapshotRepository);
    }

    @Nested
    @DisplayName("Lock acquisition and release")
    class LockBehavior {
        @Test
        @DisplayName("Successful lock acquisition persists snapshot and releases lock")
        void happyPath() throws Exception {
            // arrange
            ProbeJobMessage msg = new ProbeJobMessage();
            msg.setJobId("BATCH-PRB-20240523-USE1-01");
            msg.setDeviceId("device-123");
            msg.setProtocol("SNMP");
            when(lockMap.tryLock(eq("device-123"), eq(5L), eq(TimeUnit.SECONDS))).thenReturn(true);

            // act
            snmpWalkLockService.processSnmpWalk(msg);

            // assert snapshot persisted
            ArgumentCaptor<DeviceSnapshot> captor = ArgumentCaptor.forClass(DeviceSnapshot.class);
            verify(snapshotRepository, times(1)).save(captor.capture());
            DeviceSnapshot saved = captor.getValue();
            assertEquals("device-123", saved.getDeviceId());
            assertEquals("SNMP", saved.getProtocol());
            assertNotNull(saved.getRawPayload());
            assertNotNull(saved.getCollectedAt());

            // assert lock released
            verify(lockMap, times(1)).unlock("device-123");
        }

        @Test
        @DisplayName("Lock not acquired throws IllegalStateException and does not persist snapshot")
        void lockUnavailable() throws Exception {
            ProbeJobMessage msg = new ProbeJobMessage();
            msg.setJobId("BATCH-PRB-20240523-USE1-01");
            msg.setDeviceId("device-456");
            msg.setProtocol("SNMP");
            when(lockMap.tryLock(eq("device-456"), eq(5L), eq(TimeUnit.SECONDS))).thenReturn(false);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                    snmpWalkLockService.processSnmpWalk(msg));
            assertTrue(ex.getMessage().contains("SNMP walk lock not acquired"));

            verify(snapshotRepository, never()).save(any());
            verify(lockMap, never()).unlock(any());
        }
    }
}
