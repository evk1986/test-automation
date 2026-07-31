package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.IntegrationTestSuiteSqsReplayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class IntegrationTestSuiteSqsReplayHandler {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestSuiteSqsReplayHandler.class);
    private final IntegrationTestSuiteSqsReplayService replayService;

    public IntegrationTestSuiteSqsReplayHandler(IntegrationTestSuiteSqsReplayService replayService) {
        this.replayService = replayService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        logger.info("Received ProbeJobMessage id={}, deviceId={}", message.getId(), message.getDeviceId());
        replayService.process(message);
    }
}
