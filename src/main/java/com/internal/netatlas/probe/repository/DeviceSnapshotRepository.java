package com.internal.netatlas.probe.repository;

import com.internal.netatlas.probe.model.DeviceSnapshot;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceSnapshotRepository extends CassandraRepository<DeviceSnapshot, String> {
    // No custom methods needed for the lock‑validation test
}
