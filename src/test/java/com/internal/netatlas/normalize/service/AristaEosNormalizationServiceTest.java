package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.mapper.AristaEosMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.InterfaceRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AristaEosNormalizationServiceTest {
    @Mock
    private AristaEosMapper aristaEosMapper;

    @InjectMocks
    private AristaEosNormalizationService aristaEosNormalizationService;

    @Test
    void testNormalize() throws Exception {
        // Given
        JsonNode jsonNode = new ObjectMapper().readTree("{\"interfaceName\": \"GigabitEthernet1\", \"operationalStatus\": \"up\"}");
        InterfaceRecord interfaceRecord = new InterfaceRecord();
        interfaceRecord.setName("GigabitEthernet1");
        interfaceRecord.setOperationalStatus("up");
        NormalizedRecord expectedNormalizedRecord = new NormalizedRecord();
        expectedNormalizedRecord.setInterfaceRecord(interfaceRecord);

        // When
        NormalizedRecord actualNormalizedRecord = aristaEosNormalizationService.normalize(jsonNode);

        // Then
        assertEquals(expectedNormalizedRecord.getInterfaceRecord().getName(), actualNormalizedRecord.getInterfaceRecord().getName());
        assertEquals(expectedNormalizedRecord.getInterfaceRecord().getOperationalStatus(), actualNormalizedRecord.getInterfaceRecord().getOperationalStatus());
    }
}