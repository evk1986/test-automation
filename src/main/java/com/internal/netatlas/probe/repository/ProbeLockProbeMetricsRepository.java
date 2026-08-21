package com.internal.netatlas.probe.repository;

import com.internal.netatlas.probe.model.ProbeJob;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository;
public interface ProbeLockProbeMetricsRepository extends CassandraRepository<ProbeJob, String> {
    List<ProbeJob> findByBatchIdAndStatus(String batchId, String status);
    List<ProbeJob> findByDeviceId(String deviceId);
}