package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfHandlerDeploymentService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class NetconfSubtreeHandlerTest {

    @Test
    void handleInvokesDeploymentService() {
        NetconfHandlerDeploymentService mockService = mock(NetconfHandlerDeploymentService.class);
        NetconfSubtreeHandler handler = new NetconfSubtreeHandler(mockService);

        ProbeJobMessage message = new ProbeJobMessage(
                "job-1",
                "device-123",
                "NETCONF",
                "prod-use1",
                "BATCH-PRB-20240523-USE1-01"
        );

        handler.handle(message);

        verify(mockService, times(1)).deployHandler("BATCH-PRB-20240523-USE1-01");
    }
}
