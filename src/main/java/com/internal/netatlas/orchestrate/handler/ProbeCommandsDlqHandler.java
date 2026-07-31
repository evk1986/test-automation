package com.internal.netatlas.orchestrate.handler;

import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.internal.netatlas.orchestrate.service.DlqDrainService;

@Service
@RequiredArgsConstructor
public class ProbeCommandsDlqHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProbeCommandsDlqHandler.class);
    private final DlqDrainService dlqDrainService;

    @SqsListener("probe.commands.dlq")
    public void handle(String dlqMessage) {
        logger.info("Received DLQ message for probe.commands: {}", dlqMessage);
        dlqDrainService.processDlqMessage(dlqMessage);
    }
}
