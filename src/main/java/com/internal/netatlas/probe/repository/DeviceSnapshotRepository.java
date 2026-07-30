package com.internal.netatlas.probe.repository;

import com.internal.netatlas.probe.model.DeviceSnapshot;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Cassandra repository for persisting raw device snapshots collected by the probe service.
 */
@Repository
public interface DeviceSnapshotRepository extends CassandraRepository<DeviceSnapshot, String> {
    // No custom methods required for the current use‑case.
}
