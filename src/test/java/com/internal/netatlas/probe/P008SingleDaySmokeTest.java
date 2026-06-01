package com/internal/netatlas/probe;

import com.internal.netatlas.probe.handler.P008SingleDaySmokeHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class P008SingleDaySmokeTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private P008SingleDaySmokeHandler handler;

    @Test
    public void testHandle() throws Exception {
        // Create a test message
        ProbeJobMessage message = new ProbeJobMessage();
        message.setProtocol("NETCONF");

        // Call the handle method
        handler.handle(message);
    }
}