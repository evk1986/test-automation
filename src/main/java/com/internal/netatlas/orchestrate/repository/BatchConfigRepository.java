package com.internal.netatlas.orchestrate.repository;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchConfigRepository extends CrudRepository<BatchConfig, Long> {

    List<BatchConfig> findByRapidPollQueueDepthThreshold(Integer threshold);
}