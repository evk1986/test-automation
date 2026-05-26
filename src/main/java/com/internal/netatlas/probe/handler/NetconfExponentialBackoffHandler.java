package com.internal.netatlas.probe.handler;

import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfExponentialBackoffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class NetconfExponentialBackoffHandler {

    private final NetconfExponentialBackoffService netconfExponentialBackoffService;

    @Autowired
    public NetconfExponentialBackoffHandler(NetconfExponentialBackoffService netconfExponentialBackoffService) {
        this.netconfExponentialBackoffService = netconfExponentialBackoffService;
    }

    @SqsListener(value = "probe.commands", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    @SendTo("platform.results.dlq")
    public void handle(ProbeJobMessage message) {
        netconfExponentialBackoffService.retryWithExponentialBackoff(message);
    }
}