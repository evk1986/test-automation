package com.internal.netatlas.probe.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.SnmpAdapter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComCompanyDeviceprobeLockComServiceTest {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ILock lock;

    @Mock
    private SnmpAdapter snmpAdapter;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private ComCompanyDeviceprobeLockComService service;

    private ProbeJobMessage message;

    @BeforeEach
    void setUp() {
        message = new ProbeJobMessage();
        message.setDeviceId("device-123");
        message.setRegion("us-east-1");
        message.setBatchId("BATCH-PRB-20240523-USE1-01");

        when(hazelcastInstance.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        when(meterRegistry.counter(eq("probe.protocol.failures"), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(counter);
    }

    @Test
    void shouldAcquireLockAndExecuteSnmpWalkSuccessfully() {
        // No exception from the adapter – normal path
        doNothing().when(snmpAdapter).walk(anyString(), anyString());

        service.process(message);

        verify(hazelcastInstance).getLock("PROBE_LOCKS:device-123");
        verify(lock).tryLock();
        verify(snmpAdapter).walk("device-123", "us-east-1");
        verify(counter, never()).increment();
        verify(lock).unlock();
    }

    @Test
    void shouldIncrementFailureCounterWhenSnmpAdapterThrows() {
        doThrow(new RuntimeException("SNMP timeout"))
                .when(snmpAdapter).walk(anyString(), anyString());

        service.process(message);

        verify(snmpAdapter).walk("device-123", "us-east-1");
        verify(counter).increment();
        verify(lock).unlock();
    }

    @Test
    void shouldSkipProcessingWhenLockNotAcquired() {
        when(lock.tryLock()).thenReturn(false);

        service.process(message);

        verify(snmpAdapter, never()).walk(anyString(), anyString());
        verify(counter, never()).increment();
        verify(lock, never()).unlock(); // unlock not called because lock was never held
    }
}
