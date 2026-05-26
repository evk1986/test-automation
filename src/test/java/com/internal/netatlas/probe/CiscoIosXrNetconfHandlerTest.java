package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.CiscoIosXrNetconfHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CiscoIosXrNetconfHandlerTest {
    @InjectMocks
    private CiscoIosXrNetconfHandler ciscoIosXrNetconfHandler;

    @Test
    public void testHandle() {
        ProbeJobMessage message = new ProbeJobMessage();
        ciscoIosXrNetconfHandler.handle(message);
        // Assert that the raw response is published to SQS queue
    }
}