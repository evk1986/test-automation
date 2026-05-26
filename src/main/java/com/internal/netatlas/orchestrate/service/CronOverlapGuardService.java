package com.internal.netatlas.orchestrate.service;

import com.internal.netatlas.orchestrate.job.DailySweepJob;
import com.internal.netatlas.orchestrate.job.RapidPollJob;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class CronOverlapGuardService {

    public boolean isCronOverlapAllowed(DailySweepJob job) {
        // Check if the rapid-poll queue depth is below the threshold
        if (getRapidPollQueueDepth() < 1000) {
            // Check if the last daily sweep job was completed more than 24 hours ago
            if (getLastDailySweepJobCompletionTime().isBefore(LocalDateTime.now().minusDays(1))) {
                return true;
            }
        }
        return false;
    }

    private int getRapidPollQueueDepth() {
        // Implement logic to get the rapid-poll queue depth
        return 500;
    }

    private LocalDateTime getLastDailySweepJobCompletionTime() {
        // Implement logic to get the last daily sweep job completion time
        return LocalDateTime.now().minusHours(2);
    }
}