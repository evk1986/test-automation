package com.internal.netatlas.normalize.handler;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.service.AristaEosGoldenFileTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AristaEosGoldenFileTestHandler {
    private final AristaEosGoldenFileTestService aristaEosGoldenFileTestService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public AristaEosGoldenFileTestHandler(AristaEosGoldenFileTestService aristaEosGoldenFileTestService, QueueMessagingTemplate queueMessagingTemplate) {
        this.aristaEosGoldenFileTestService = aristaEosGoldenFileTestService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void handleGoldenFileTest() {
        List<NormalizedRecord> normalizedRecords = aristaEosGoldenFileTestService.validateGoldenFileTests();
        queueMessagingTemplate.convertAndSend("normalize.ingest", normalizedRecords);
    }
}