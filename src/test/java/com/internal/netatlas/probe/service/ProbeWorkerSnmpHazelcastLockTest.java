package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProbeWorkerSnmpHazelcastLockTest {

    private HazelcastInstance hazelcastInstance;
    private ILock lock;
    private SqsClient sqsClient;
    private MeterRegistry meterRegistry;
    private Counter counter;
    private ProbeWorkerSnmpHazelcastLock worker;

    @BeforeEach
    void setUp() {
        hazelcastInstance = mock(HazelcastInstance.class);
        lock = mock(ILock.class);
        sqsClient = mock(SqsClient.class);
        meterRegistry = mock(MeterRegistry.class);
        counter = mock(Counter.class);

        when(meterRegistry.counter("snmp.lock.contention")).thenReturn(counter);
        when(hazelcastInstance.getLock(anyString())).thenReturn(lock);
        when(sqsClient.changeMessageVisibility(any())).thenReturn(ChangeMessageVisibilityResponse.builder().build());

        worker = new ProbeWorkerSnmpHazelcastLock(hazelcastInstance, sqsClient, meterRegistry);
    }

    @Test
    void whenLockAcquired_thenVisibilityTimeoutSet() {
        when(lock.tryLock()).thenReturn(true);
        ProbeJob job = mock(ProbeJob.class);
        when(job.getDeviceId()).thenReturn("device-123");
        when(job.getQueueUrl()).thenReturn("https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands");

        worker.executeSnmpWalk(job, "receipt-handle-abc");

        verify(lock).unlock();
        verify(sqsClient).changeMessageVisibility(any());
        verify(counter, never()).increment();
    }

    @Test
    void whenLockNotAcquired_thenCounterIncrementedAndNoVisibilityChange() {
        when(lock.tryLock()).thenReturn(false);
        ProbeJob job = mock(ProbeJob.class);
        when(job.getDeviceId()).thenReturn("device-456");
        when(job.getQueueUrl()).thenReturn("https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands");

        worker.executeSnmpWalk(job, "receipt-handle-def");

        verify(counter).increment();
        verify(sqsClient, never()).changeMessageVisibility(any());
        verify(lock, never()).unlock();
    }
}
