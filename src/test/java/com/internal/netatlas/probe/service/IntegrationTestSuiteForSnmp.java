package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.handler.SnmpWalkJobHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SNMP walk lock service and its SQS handler.
 */
public class IntegrationTestSuiteForSnmp {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ILock mockLock;

    private SnmpWalkLockService snmpWalkLockService;

    private SnmpWalkJobHandler snmpWalkJobHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(hazelcastInstance.getLock(anyString())).thenReturn(mockLock);
        snmpWalkLockService = new SnmpWalkLockService(hazelcastInstance);
        snmpWalkJobHandler = new SnmpWalkJobHandler(snmpWalkLockService);
    }

    @Test
    void testHandlerDelegatesToService() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setDeviceId("device-123");
        // Spy on the service to verify delegation
        SnmpWalkLockService spyService = spy(snmpWalkLockService);
        SnmpWalkJobHandler handler = new SnmpWalkJobHandler(spyService);
        handler.handle(msg);
        verify(spyService, times(1)).processProbeJob(msg);
    }

    @Test
    void testLockAcquisitionAndRelease() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setDeviceId("device-456");
        snmpWalkLockService.processProbeJob(msg);
        // Verify that lock was obtained with the correct key
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(hazelcastInstance, times(1)).getLock(keyCaptor.capture());
        assertEquals("snmp-walk-device-456", keyCaptor.getValue());
        // Verify lock lifecycle
        verify(mockLock, times(1)).lock();
        verify(mockLock, times(1)).unlock();
    }
}
