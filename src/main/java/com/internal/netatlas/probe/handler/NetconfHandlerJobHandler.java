package com.internal.netatlas.probe.handler;

import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import com.internal.netatlas.probe.service.NetconfHandlerService;
import com.internal.netatlas.probe.model.NetconfJobMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetconfHandlerJobHandler {

    private final NetconfHandlerService netconfHandlerService;

    @SqsListener("probe.commands")
    public void handle(NetconfJobMessage message) {
        log.info("Received NETCONF job {} for device {}", message.getJobId(), message.getDeviceId());
        netconfHandlerService.process(message);
    }
}
