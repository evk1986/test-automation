package com.internal.netatlas.normalize;

import com.internal.netatlas.normalize.handler.AristaEosGoldenFileTestHandler;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.service.AristaEosGoldenFileTestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AristaEosGoldenFileTestHandlerTest {
    @Mock
    private AristaEosGoldenFileTestService aristaEosGoldenFileTestService;

    @InjectMocks
    private AristaEosGoldenFileTestHandler aristaEosGoldenFileTestHandler;

    @Test
    void testHandleGoldenFileTest() {
        aristaEosGoldenFileTestHandler.handleGoldenFileTest();
        verify(aristaEosGoldenFileTestService).validateGoldenFileTests();
    }
}