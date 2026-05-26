package com.internal.netatlas.normalize;

import com.internal.netatlas.normalize.mapper.CiscoIosXrNetconfMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.service.NetconfNormalizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class NetconfNormalizationServiceTest {

    @Mock
    private CiscoIosXrNetconfMapper ciscoIosXrNetconfMapper;

    @InjectMocks
    private NetconfNormalizationService netconfNormalizationService;

    @Test
    void testNormalize() {
        // Arrange
        JsonNode netconfResponse = null; // Initialize with a valid JSON node

        // Act
        NormalizedRecord normalizedRecord = netconfNormalizationService.normalize(netconfResponse);

        // Assert
        assertNotNull(normalizedRecord);
    }
}