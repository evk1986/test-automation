package com.internal.netatlas.probe.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import com.internal.netatlas.probe.handler.NetconfJobProcessor;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class NetconfDlqReplayServiceTest {

    private AmazonSQS sqsClient;
    private ProbeJobRepository jobRepository;
    private NetconfJobProcessor processor;
    private HazelcastInstance hazelcastInstance;
    private FencedLock lock;
    private NetconfDlqReplayService service;

    @BeforeEach
    void setUp() {
        sqsClient = mock(AmazonSQS.class);
        jobRepository = mock(ProbeJobRepository.class);
        processor = mock(NetconfJobProcessor.class);
        hazelcastInstance = mock(HazelcastInstance.class);
        lock = mock(FencedLock.class);
        when(hazelcastInstance.getCPSubsystem().getLock(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        service = new NetconfDlqReplayService(sqsClient, jobRepository, processor,
                hazelcastInstance, new SimpleMeterRegistry());
    }

    @Test
    void replayFailedMessages_processesMessageAndIncrementsMetric() throws Exception {
        ProbeJob job = new ProbeJob();
        job.setDeviceId("device-123");
        job.setStatus(ProbeJob.Status.FAILED);
        String body = new ObjectMapper().writeValueAsString(job);
        Message msg = new Message().withBody(body).withReceiptHandle("rh-1");
        ReceiveMessageResult result = new ReceiveMessageResult().withMessages(Collections.singletonList(msg));
        when(sqsClient.receiveMessage(any())).thenReturn(result);

        service.replayFailedMessages();

        verify(lock).tryLock();
        verify(processor).process(job);
        verify(sqsClient).deleteMessage(anyString(), eq("rh-1"));
    }

    @Test
    void replayFailedMessages_skipsAlreadySuccessfulJob() throws Exception {
        ProbeJob job = new ProbeJob();
        job.setDeviceId("device-456");
        job.setStatus(ProbeJob.Status.SUCCESS);
        String body = new ObjectMapper().writeValueAsString(job);
        Message msg = new Message().withBody(body).withReceiptHandle("rh-2");
        ReceiveMessageResult result = new ReceiveMessageResult().withMessages(Collections.singletonList(msg));
        when(sqsClient.receiveMessage(any())).thenReturn(result);

        service.replayFailedMessages();

        verify(lock, never()).tryLock();
        verify(processor, never()).process(any());
        verify(sqsClient).deleteMessage(anyString(), eq("rh-2"));
    }
}
