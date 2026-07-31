package com.internal.netatlas.probe.service;

import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SnmpWalkServiceTest {

    private ProbeJobRepository jobRepository;
    private ILock snmpWalkLock;
    private SnmpWalkService snmpWalkService;

    @BeforeEach
    void setUp() {
        jobRepository = mock(ProbeJobRepository.class);
        snmpWalkLock = mock(ILock.class);
        snmpWalkService = new SnmpWalkService(jobRepository, snmpWalkLock);
    }

    @Test
    void performWalk_acquiresLockAndMarksSuccess() throws Exception {
        ProbeJob job = new ProbeJob();
        job.setDeviceId("device-123");
        when(snmpWalkLock.tryLock()).thenReturn(true);
        when(snmpWalkLock.isHeldByCurrentThread()).thenReturn(true);

        snmpWalkService.performWalk(job);

        assertEquals("SUCCESS", job.getStatus());
        InOrder inOrder = inOrder(snmpWalkLock, jobRepository);
        inOrder.verify(snmpWalkLock).tryLock();
        inOrder.verify(snmpWalkLock).unlock();
        inOrder.verify(jobRepository).save(job);
    }

    @Test
    void performWalk_lockNotAcquiredMovesToDlq() {
        ProbeJob job = new ProbeJob();
        job.setDeviceId("device-456");
        when(snmpWalkLock.tryLock()).thenReturn(false);

        snmpWalkService.performWalk(job);

        assertEquals("DLQ", job.getStatus());
        verify(snmpWalkLock, never()).unlock();
        verify(jobRepository).save(job);
    }
}
