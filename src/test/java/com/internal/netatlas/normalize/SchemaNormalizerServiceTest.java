package com.internal.netatlas.normalize;

import com.internal.netatlas.normalize.mapper.AristaEosEapiMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.JsonNode;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SchemaNormalizerServiceTest {

    @Mock
    private AristaEosEapiMapper aristaEosEapiMapper;

    @InjectMocks
    private SchemaNormalizerService schemaNormalizerService;

    @Test
    void testNormalize() {
        JsonNode eapiResponse = JsonNode.fromJson("{\"interfaceName\": \"GigabitEthernet1\", \"operationalStatus\": \"UP\"}");
        NormalizedRecord expectedRecord = new NormalizedRecord();
        expectedRecord.setCanonicalInterface(new CanonicalInterface("GigabitEthernet1", "UP"));
        NormalizedRecord actualRecord = schemaNormalizerService.normalize(eapiResponse);
        assertEquals(expectedRecord, actualRecord);
    }
}