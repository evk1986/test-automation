package com.internal.netatlas.probe.repository;

import com.internal.netatlas.probe.model.ProbeJob;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProbeJobRepository extends CassandraRepository<ProbeJob, String> {

    List<ProbeJob> findByBatchId(String batchId);

    List<ProbeJob> findByDeviceIdAndStatus(String deviceId, String status);
}
