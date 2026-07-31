package com.internal.netatlas.probe.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Stub repository used only to verify that the local Hazelcast client can obtain a Cassandra session.
 * The entity type is a placeholder; the table is not required for the onboarding verification.
 */
@Repository
public interface ProbeLockLogRepository extends CassandraRepository<Object, String> {
    // No custom methods needed for the onboarding check.
}
