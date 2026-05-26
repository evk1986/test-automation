package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfSessionRetryHandler;
import com.internal.netatlas.probe.model.ProbeJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NetconfSessionRetryHandlerTest {
    @InjectMocks
    private NetconfSessionRetryHandler netconfSessionRetryHandler;

    @Test
    public void testHandle() {
        // Test the handle method with a sample ProbeJob
        ProbeJob probeJob = new ProbeJob();
        netconfSessionRetryHandler.handle(probeJob);
        // Verify the expected behavior
        assertEquals(5, netconfSessionRetryHandler.getRetryCount());
    }
}