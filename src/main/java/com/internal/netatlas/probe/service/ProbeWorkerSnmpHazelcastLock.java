package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import org.springframework.stereotype.Service;

@Service
public class ProbeWorkerSnmpHazelcastLock {

    private final HazelcastInstance hazelcastInstance;
    private final SqsClient sqsClient;
    private final Counter contentionCounter;

    public ProbeWorkerSnmpHazelcastLock(HazelcastInstance hazelcastInstance,
                                        SqsClient sqsClient,
                                        MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.sqsClient = sqsClient;
        this.contentionCounter = meterRegistry.counter("snmp.lock.contention");
    }

    public void executeSnmpWalk(ProbeJob job, String receiptHandle) {
        String lockKey = "snmpLock-" + job.getDeviceId();
        ILock lock = hazelcastInstance.getLock(lockKey);
        boolean acquired = lock.tryLock();
        if (!acquired) {
            // Increment contention metric and exit
            contentionCounter.increment();
            return;
        }
        try {
            // Set SQS visibility timeout to keep lock duration
            ChangeMessageVisibilityRequest visibilityRequest = ChangeMessageVisibilityRequest.builder()
                    .queueUrl(job.getQueueUrl())
                    .receiptHandle(receiptHandle)
                    .visibilityTimeoutSeconds(300)
                    .build();
            sqsClient.changeMessageVisibility(visibilityRequest);
            // TODO: actual SNMP walk implementation would go here
        } finally {
            lock.unlock();
        }
    }
}
