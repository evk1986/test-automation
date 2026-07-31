package com.internal.netatlas.probe.repository;

import com.hazelcast.cp.lock.FencedLock;
import org.springframework.stereotype.Repository;

/**
 * Repository abstraction over Hazelcast lock objects used by the Device‑Probe service.
 */
@Repository
public interface ProbeLockRepository {

    /**
     * Retrieves (or creates) a distributed lock for the given batch identifier.
     */
    FencedLock getLock(String batchId);
}
