package com/internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class P008SingleDaySmokeService {
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    public void processMessage(ProbeJobMessage message) {
        // Process the message and send it to the next queue
        queueMessagingTemplate.convertAndSend("normalize.ingest", message);
    }
}