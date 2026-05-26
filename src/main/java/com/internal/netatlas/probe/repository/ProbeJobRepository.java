package com/internal/netatlas/probe/repository;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProbeJobRepository extends CrudRepository<ProbeJobMessage, String> {
    // Implement repository methods as needed
}