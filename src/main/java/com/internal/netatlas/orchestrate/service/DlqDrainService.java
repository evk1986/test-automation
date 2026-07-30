package com.internal.netatlas.orchestrate.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DlqDrainService {
    private static final Logger logger = LoggerFactory.getLogger(DlqDrainService.class);

    public void processDlqMessage(String message) {
        if (message != null && message.contains("jobId")) {
            String jobId = extractJobId(message);
            logger.info("Processing DLQ message for job {}", jobId);
            // In a real implementation we would call the CLI or internal API to reset visibility timeout or re‑queue the job.
        } else {
            logger.warn("DLQ message does not contain jobId: {}", message);
        }
    }

    private String extractJobId(String message) {
        int start = message.indexOf("\"jobId\":\"");
        if (start == -1) {
            return "unknown";
        }
        start += 9;
        int end = message.indexOf('"', start);
        return end == -1 ? "unknown" : message.substring(start, end);
    }
}
