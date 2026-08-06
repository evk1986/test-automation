package com.internal.netatlas.probe.repository;

import com.internal.netatlas.probe.model.DeviceSnapshot;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Cassandra repository for persisting raw device snapshots collected via NETCONF.
 */
@Repository
public interface DeviceSnapshotRepository extends CassandraRepository<DeviceSnapshot, String> {
    // No custom queries required for the current slice.
}
