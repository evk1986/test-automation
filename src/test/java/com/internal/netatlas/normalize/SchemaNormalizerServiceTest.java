package com.internal.netatlas.normalize;

import com.internal.netatlas.normalize.mapper.CiscoIosXrNetconfMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.service.SchemaNormalizerService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class SchemaNormalizerServiceTest {
    @Mock
    private CiscoIosXrNetconfMapper mapper;

    @InjectMocks
    private SchemaNormalizerService service;

    @Test
    void testNormalize() {
        // Arrange
        JSONObject netconfResponse = new JSONObject();
        NormalizedRecord expected = new NormalizedRecord();

        // Act
        NormalizedRecord actual = service.normalize(netconfResponse);

        // Assert
        assertNotNull(actual);
    }
}