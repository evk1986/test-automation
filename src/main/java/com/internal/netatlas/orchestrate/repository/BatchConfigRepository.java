package com.internal.netatlas.orchestrate.repository;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchConfigRepository extends CrudRepository<BatchConfig, String> {
}