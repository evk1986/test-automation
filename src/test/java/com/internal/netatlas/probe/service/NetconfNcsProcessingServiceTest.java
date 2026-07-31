package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceInfo;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.protocol.NetconfTimeoutException;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NetconfNcsProcessingServiceTest {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ILock lock;

    @Mock
    private NetconfAdapter netconfAdapter;

    @Mock
    private ProbeJobRepository jobRepository;

    private MeterRegistry meterRegistry;

    @InjectMocks
    private NetconfNcsProcessingService processingService;

    private ProbeJobMessage sampleMessage;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        processingService = new NetconfNcsProcessingService(hazelcastInstance, netconfAdapter, jobRepository, meterRegistry);
        when(hazelcastInstance.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        sampleMessage = ProbeJobMessage.builder()
                .jobId("JOB-NETCONF-4821")
                .deviceId("NCS-01")
                .protocol("NETCONF")
                .region("us-east-1")
                .batchId("BATCH-PRB-20240523-USE1-01")
                .build();
    }

    @Test
    void process_successfulFetch_updatesJobAndReleasesLock() throws Exception {
        String raw = "<config>...</config>";
        when(netconfAdapter.fetchSubtree("NCS-01", "ncs-config")).thenReturn(raw);
        // Stub DeviceInfo mapping – static method used in service.
        mockStatic(DeviceInfo.class).when(() -> DeviceInfo.fromRaw(raw)).thenReturn(new DeviceInfo());

        processingService.process(sampleMessage);

        verify(lock).unlock();
        verify(jobRepository).save(argThat(job -> "SUCCESS".equals(job.getStatus())));
        assertEquals(0, meterRegistry.get("probe.protocol.failures").counter().count());
    }

    @Test
    void process_timeoutIncrementsFailureCounter_andMarksJobFailed() throws Exception {
        when(netconfAdapter.fetchSubtree("NCS-01", "ncs-config"))
                .thenThrow(new NetconfTimeoutException("Timeout after 30s"));

        processingService.process(sampleMessage);

        verify(lock).unlock();
        verify(jobRepository).save(argThat(job -> "FAILED".equals(job.getStatus())));
        assertEquals(1, meterRegistry.get("probe.protocol.failures").counter().count());
    }
}
