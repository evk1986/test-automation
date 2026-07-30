package com.internal/netatlas/probe/repository;

import com.datastax.driver.core.utils.UUIDs;
import com.internal.netatlas.probe.model.ProbeJob;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProbeJobRepository extends CassandraRepository<ProbeJob, String> {
    Iterable<ProbeJob> findByBatchIdAndStatus(String batchId, String status);
}