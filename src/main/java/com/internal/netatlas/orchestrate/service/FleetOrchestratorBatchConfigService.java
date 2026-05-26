package com.internal.netatlas.orchestrate.service;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import com.internal.netatlas.orchestrate.repository.BatchConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FleetOrchestratorBatchConfigService {
    private final BatchConfigRepository batchConfigRepository;

    @Autowired
    public FleetOrchestratorBatchConfigService(BatchConfigRepository batchConfigRepository) {
        this.batchConfigRepository = batchConfigRepository;
    }

    public BatchConfig getBatchConfig(String batchId) {
        Optional<BatchConfig> batchConfig = batchConfigRepository.findById(batchId);
        return batchConfig.orElse(null);
    }
}