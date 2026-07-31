package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.IntegrationTestSuiteSqsReplayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class IntegrationTestSuiteSqsReplayHandlerTest {

    @Mock
    private IntegrationTestSuiteSqsReplayService replayService;

    private IntegrationTestSuiteSqsReplayHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new IntegrationTestSuiteSqsReplayHandler(replayService);
    }

    @Test
    void shouldDelegateMessageToService() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setId("msg-1");
        msg.setDeviceId("device-123");
        handler.handle(msg);
        verify(replayService, times(1)).process(msg);
    }
}
