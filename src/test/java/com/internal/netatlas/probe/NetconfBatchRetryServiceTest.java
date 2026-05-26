package com.internal.netatlas.probe;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NetconfBatchRetryServiceTest {

    @Mock
    private ProbeJobRepository probeJobRepository;

    @InjectMocks
    private NetconfBatchRetryService retryService;

    @Test
    void testUpdateJobStatus() {
        // Test the updateJobStatus method
        String deviceId = "device-1";
        String response = "response-1";
        retryService.updateJobStatus(deviceId, response);
        verify(probeJobRepository).save(new ProbeJobMessage(deviceId, "SUCCESS"));
    }
}