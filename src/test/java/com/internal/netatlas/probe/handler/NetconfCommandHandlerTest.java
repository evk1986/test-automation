package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NetconfCommandHandlerTest {

    @Mock
    private NetconfSessionService sessionService;

    @InjectMocks
    private NetconfCommandHandler commandHandler;

    @Test
    void handle_ValidMessage_ExecutesSubtreeQuery() {
        ProbeJobMessage message = new ProbeJobMessage();
        message.setJobId("JOB-NETCONF-4821");
        message.setDeviceId("cr01.sjc10.net");
        message.setBatchId("BATCH-PRB-20240523-USE1-01");

        commandHandler.handle(message);

        verify(sessionService).executeSubtreeQuery("JOB-NETCONF-4821", "cr01.sjc10.net", "BATCH-PRB-20240523-USE1-01");
    }

    @Test
    void handle_NullMessage_DoesNotInvokeService() {
        commandHandler.handle(null);

        verifyNoInteractions(sessionService);
    } 
}
