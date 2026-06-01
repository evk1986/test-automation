package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SnmpWalkJobHandler {

    private final NetconfBatchRetryService netconfBatchRetryService;
    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public SnmpWalkJobHandler(NetconfBatchRetryService netconfBatchRetryService, HazelcastInstance hazelcastInstance) {
        this.netconfBatchRetryService = netconfBatchRetryService;
        this.hazelcastInstance = hazelcastInstance;
    }

    @SqsListener(value = "probe.commands", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void handle(@Payload ProbeJob job, @Headers SqsMessageHeaders headers) {
        ILock lock = hazelcastInstance.getLock("snmp-walk-job-lock");
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                // process the job
                netconfBatchRetryService.retryFailedJobs(job.getBatchId());
            } finally {
                lock.unlock();
            }
        }
    }
}