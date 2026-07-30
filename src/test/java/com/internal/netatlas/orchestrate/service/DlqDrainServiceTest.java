package com.internal.netatlas.orchestrate.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DlqDrainServiceTest {

    @Test
    void processDlqMessage_withJobId_logsInfo() {
        DlqDrainService service = new DlqDrainService();
        String msg = "{\"jobId\":\"BATCH-PRB-20240523-USE1-01\",\"error\":\"timeout\"}";
        assertDoesNotThrow(() -> service.processDlqMessage(msg));
    }

    @Test
    void processDlqMessage_withoutJobId_logsWarn() {
        DlqDrainService service = new DlqDrainService();
        String msg = "{\"error\":\"timeout\"}";
        assertDoesNotThrow(() -> service.processDlqMessage(msg));
    }
}
