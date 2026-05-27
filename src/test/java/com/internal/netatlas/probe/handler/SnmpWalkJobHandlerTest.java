package com.internal.netatlas.probe.handler;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.SnmppWalkJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SnmpWalkJobHandlerTest {

    @Mock
    private SnmppWalkJobService snmppWalkJobService;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @InjectMocks
    private SnmpWalkJobHandler handler;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(handler, "hazelcastInstance", hazelcastInstance);
    }

    @Test
    void testHandle() {
        ProbeJobMessage message = new ProbeJobMessage();
        message.setDeviceId("device-id");
        message.setBatchId("batch-id");
        handler.handle(message);
        verify(snmppWalkJobService).processSnmpWalkJob(message);
    }
}