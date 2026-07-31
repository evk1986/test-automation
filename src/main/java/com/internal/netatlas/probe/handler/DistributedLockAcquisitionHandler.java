package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.LockRequestMessage;
import com.internal.netatlas.probe.service.DistributedLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DistributedLockAcquisitionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedLockAcquisitionHandler.class);
    private final DistributedLockService lockService;

    @Autowired
    public DistributedLockAcquisitionHandler(DistributedLockService lockService) {
        this.lockService = lockService;
    }

    @SqsListener("probe.commands")
    public void handle(LockRequestMessage message) {
        LOG.info("Received lock request: jobId={}, deviceId={}, lockId={}",
                message.getJobId(), message.getDeviceId(), message.getLockId());
        String lockKey = String.format("lock:%s:%s", message.getJobId(), message.getLockId());
        boolean acquired = lockService.acquireLock(lockKey);
        if (acquired) {
            LOG.info("Lock acquired for key {}", lockKey);
        } else {
            LOG.warn("Failed to acquire lock for key {} – message will be retried or sent to DLQ", lockKey);
        }
    }
}
