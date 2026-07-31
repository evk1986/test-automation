package com.internal.netatlas.probe.handler.netconf;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProbeHandlersNetconfSubtreeHandlerTest {

    @Mock
    private ProbeHandlersNetconfSubtreeService service;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ILock lock;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    private ProbeHandlersNetconfSubtreeHandler handler;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(counter);
        when(hazelcastInstance.getLock(anyString())).thenReturn(lock);
        handler = new ProbeHandlersNetconfSubtreeHandler(service, hazelcastInstance, meterRegistry);
    }

    @Test
    void handle_acquiresLockAndProcessesMessage() throws Exception {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("NETCONF");
        msg.setDeviceFamily("IOS-XR");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");
        msg.setDeviceId("device-123");
        msg.setRawPayload("<rpc-reply><data><hostname>router1</hostname></data></rpc-reply>");

        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);

        handler.handle(msg);

        verify(lock).unlock();
        verify(service).process(msg);
        verify(counter, never()).increment();
    }

    @Test
    void handle_lockNotAcquiredIncrementsFailureCounter() throws Exception {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("NETCONF");
        msg.setDeviceFamily("IOS-XR");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");
        msg.setDeviceId("device-124");
        msg.setRawPayload("<rpc-reply/>);

        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(false);

        handler.handle(msg);

        verify(service, never()).process(any());
        verify(counter).increment();
    }

    @Test
    void handle_exceptionInServiceIncrementsFailureCounter() throws Exception {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("NETCONF");
        msg.setDeviceFamily("IOS-XR");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");
        msg.setDeviceId("device-125");
        msg.setRawPayload("<invalid/>");

        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        doThrow(new IllegalStateException("parse error")).when(service).process(msg);

        handler.handle(msg);

        verify(service).process(msg);
        verify(counter).increment();
        verify(lock).unlock();
    }
}
