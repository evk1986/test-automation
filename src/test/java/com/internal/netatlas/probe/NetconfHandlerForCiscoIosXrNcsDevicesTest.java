package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfHandlerForCiscoIosXrNcsDevices;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

@ExtendWith(MockitoExtension.class)
public class NetconfHandlerForCiscoIosXrNcsDevicesTest {
    @InjectMocks
    private NetconfHandlerForCiscoIosXrNcsDevices netconfHandler;

    @Test
    public void testHandleNetconfMessage() {
        // Test the handleNetconfMessage method
        ProbeJobMessage message = new ProbeJobMessage("NETCONF", "Cisco IOS-XR NCS", "device-id");
        netconfHandler.handleNetconfMessage(message);
    }
}