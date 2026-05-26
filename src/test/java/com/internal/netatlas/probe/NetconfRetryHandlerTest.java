package com.internal/netatlas/probe;

import com.amazonaws.services.sqs.model.Message;
import com.internal/netatlas/probe.handler.NetconfRetryHandler;
import com.internal/netatlas/probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetconfRetryHandlerTest {
    @Mock
    private NetconfRetryService netconfRetryService;
    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;
    @InjectMocks
    private NetconfRetryHandler netconfRetryHandler;

    @Test
    public void testHandle() {
        // setup
        Message message = new Message();
        ProbeJobMessage probeJobMessage = new ProbeJobMessage(message);
        probeJobMessage.setProtocol("NETCONF");
        // execute
        netconfRetryHandler.handle(message);
        // verify
        // verify(netconfRetryService).retryFailedJobs(probeJobMessage.getBatchId());
    }
}