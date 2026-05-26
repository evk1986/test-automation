package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.normalize.model.CanonicalInterface;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AristaEosEapiMapperTest {

    @Test
    public void testMapToCanonicalInterface() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode eapiResponse = objectMapper.readTree("{\"interfaceName\": \"GigabitEthernet1\", \"operationalStatus\": \"up\"}");
        AristaEosEapiMapper mapper = new AristaEosEapiMapper();
        NormalizedRecord normalizedRecord = mapper.mapToCanonicalInterface(eapiResponse);
        assertEquals("GigabitEthernet1", normalizedRecord.getInterfaceRecord().getInterfaceName());
        assertEquals("up", normalizedRecord.getInterfaceRecord().getOperationalStatus());
    }
}