package com.internal.netatlas.probe.handler;

import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import com.internal.netatlas.probe.service.NetconfHandlerDeploymentService;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NetconfSubtreeHandler {

    private static final Logger logger = LoggerFactory.getLogger(NetconfSubtreeHandler.class);
    private final NetconfHandlerDeploymentService deploymentService;

    public NetconfSubtreeHandler(NetconfHandlerDeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        logger.info("Received ProbeJobMessage id={}, deviceId={}", message.getId(), message.getDeviceId());
        deploymentService.deployHandler(message.getBatchId());
        // In a full implementation a result would be published to an SNS topic here
    }
}
