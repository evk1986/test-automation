package com.internal.netatlas.normalize.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AristaEosNormalizationServiceTest {

    private final AristaEosNormalizationService service = new AristaEosNormalizationService();

    @Test
    void normalizeInterface_parsesRawPayload() {
        String raw = "Ethernet1,up,1000Mbps";
        String result = service.normalizeInterface(raw);
        assertTrue(result.contains("\"interfaceName\":\"Ethernet1\""));
        assertTrue(result.contains("\"adminStatus\":\"up\""));
        assertTrue(result.contains("\"speed\":\"1000Mbps\""));
        assertTrue(result.contains("\"schemaVersion\":\"v3\""));
    }

    @Test
    void normalizeInterface_handlesInvalidPayload() {
        String raw = "invalid_payload";
        String result = service.normalizeInterface(raw);
        assertTrue(result.contains("\"raw\":\"invalid_payload\""));
        assertTrue(result.contains("\"schemaVersion\":\"v3\""));
    }
}
