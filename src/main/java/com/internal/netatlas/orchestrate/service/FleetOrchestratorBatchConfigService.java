package com.internal.netatlas.orchestrate.service;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import com.internal.netatlas.orchestrate.model.CronJob;
import com.internal.netatlas.orchestrate.repository.BatchConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FleetOrchestratorBatchConfigService {

    private final BatchConfigRepository batchConfigRepository;

    @Autowired
    public FleetOrchestratorBatchConfigService(BatchConfigRepository batchConfigRepository) {
        this.batchConfigRepository = batchConfigRepository;
    }

    public void updateBatchConfig(BatchConfig batchConfig) {
        batchConfigRepository.save(batchConfig);
    }

    public void implementCronOverlapGuard(CronJob cronJob) {
        // Implement cron overlap guard logic
        List<CronJob> existingJobs = batchConfigRepository.findByCronExpression(cronJob.getCronExpression());
        if (!existingJobs.isEmpty()) {
            // Handle overlap
            System.out.println("Cron overlap detected");
        }
    }
}