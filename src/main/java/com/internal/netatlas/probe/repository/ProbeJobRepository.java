package com/internal/netatlas/probe/repository;

import com.internal.netatlas.probe.model.ProbeJob;
import org.springframework.data.repository.CrudRepository;

public interface ProbeJobRepository extends CrudRepository<ProbeJob, String> {
}