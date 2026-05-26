package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfExponentialBackoffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NetconfExponentialBackoffHandler {

    private final NetconfExponentialBackoffService netconfExponentialBackoffService;

    @Autowired
    public NetconfExponentialBackoffHandler(NetconfExponentialBackoffService netconfExponentialBackoffService) {
        this.netconfExponentialBackoffService = netconfExponentialBackoffService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        netconfExponentialBackoffService.retryWithExponentialBackoff(message);
    }
}