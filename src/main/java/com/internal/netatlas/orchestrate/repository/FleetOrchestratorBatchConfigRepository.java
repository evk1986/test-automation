package com.internal.netatlas.orchestrate.repository;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import org.springframework.data.repository.CrudRepository;

public interface FleetOrchestratorBatchConfigRepository extends CrudRepository<BatchConfig, Long> {
}