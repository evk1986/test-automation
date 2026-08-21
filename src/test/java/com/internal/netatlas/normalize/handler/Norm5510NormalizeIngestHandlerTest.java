package com.internal.netatlas.normalize.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.normalize.service.Norm5510InterfaceNormalizerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Norm5510NormalizeIngestHandlerTest {

    @Mock
    private Norm5510InterfaceNormalizerService normalizerService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private Norm5510NormalizeIngestHandler handler;

    @Test
    void testHandleAristaPayloadSuccess() {
        String message = "{\"snapshotId\":\"SNAP-101\",\"deviceFamily\":\"Arista EOS\",\"result\":[{\"interfaces\":{\"Ethernet1\":{\"description\":\"Uplink\",\"mtu\":9000}}}]}";
        assertDoesNotThrow(() -> handler.handle(message));
        verify(normalizerService).processAndNormalize(eq("SNAP-101"), eq("Arista EOS"), any());
    }

    @Test
    void testHandleSixDeviceFamilies() {
        String[] families = {
            "Cisco IOS-XE", "Cisco IOS-XR", "Cisco NX-OS",
            "Arista EOS", "Juniper JunOS", "generic SNMP CPE"
        };

        for (String family : families) {
            String message = String.format("{\"snapshotId\":\"SNAP-999\",\"deviceFamily\":\"%s\",\"interfaces\":[]}", family);
            assertDoesNotThrow(() -> handler.handle(message));
        }
    }

    @Test
    void testHandleInvalidJsonThrowsException() {
        String invalidJson = "{ invalid json payload }";
        assertThrows(RuntimeException.class, () -> handler.handle(invalidJson));
    }
}
