package com.internal.netatlas.probe.repository;

import com.internal.netatlas.probe.model.ProbeJob;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProbeLockProbeMetricsRepository extends CassandraRepository<ProbeJob, String> {

    @Query("SELECT * FROM probe_jobs WHERE device_id = ?0 ALLOW FILTERING")
    List<ProbeJob> findByDeviceId(String deviceId);

    @Query("SELECT * FROM probe_jobs WHERE batch_id = ?0 ALLOW FILTERING")
    List<ProbeJob> findByBatchId(String batchId);
}
