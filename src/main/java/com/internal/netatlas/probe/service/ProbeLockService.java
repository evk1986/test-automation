package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.repository.ProbeLockRepository;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import org.springframework.stereotype.Service;

@Service
public class ProbeLockService {

    private final ProbeLockRepository lockRepository;

    public ProbeLockService(ProbeLockRepository lockRepository) {
        this.lockRepository = lockRepository;
    }

    /**
     * Returns the current lock status for the supplied batch identifier.
     */
    public LockStatusDto getLockStatus(String batchId) {
        FencedLock lock = lockRepository.getLock(batchId);
        boolean locked = lock.isLocked();
        String owner = locked ? lock.getOwnerUuid() : null;
        // Approximate lock acquisition time using remaining lease time.
        long lockAcquiredAt = locked ? System.currentTimeMillis() - lock.getRemainingLeaseTime() : 0L;
        return new LockStatusDto(locked, owner, lockAcquiredAt);
    }

    /**
     * Simple DTO exposing lock state to callers.
     */
    public static class LockStatusDto {
        private final boolean locked;
        private final String ownerUuid;
        private final long lockAcquiredAt;

        public LockStatusDto(boolean locked, String ownerUuid, long lockAcquiredAt) {
            this.locked = locked;
            this.ownerUuid = ownerUuid;
            this.lockAcquiredAt = lockAcquiredAt;
        }

        public boolean isLocked() {
            return locked;
        }

        public String getOwnerUuid() {
            return ownerUuid;
        }

        public long getLockAcquiredAt() {
            return lockAcquiredAt;
        }
    }
}
