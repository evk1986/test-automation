package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfRetryHandler;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetconfRetryHandlerTest {
    @Mock
    private NetconfBatchRetryService netconfBatchRetryService;

    @InjectMocks
    private NetconfRetryHandler netconfRetryHandler;

    @Test
    void testHandle() {
        // given
        ProbeJob probeJob = new ProbeJob();
        probeJob.setProtocol("NETCONF");
        ProbeJobMessage message = new ProbeJobMessage(probeJob);

        // when
        netconfRetryHandler.handle(message);

        // then
        // assert logic
    }
}