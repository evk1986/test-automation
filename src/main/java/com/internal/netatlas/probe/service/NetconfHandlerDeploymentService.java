package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NetconfHandlerDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(NetconfHandlerDeploymentService.class);
    private final HazelcastInstance hazelcastInstance;

    public NetconfHandlerDeploymentService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void deployHandler(String batchId) {
        ILock lock = hazelcastInstance.getLock("netconfHandlerLock");
        logger.info("Acquiring Hazelcast lock for batch {}", batchId);
        lock.lock();
        try {
            logger.info("Deploying NETCONF handler for batch {}", batchId);
            // Simulate deployment steps such as pulling Docker image, updating config, restarting pod
            Thread.sleep(100);
            logger.info("Deployment completed for batch {}", batchId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Deployment interrupted for batch {}", batchId, e);
        } finally {
            lock.unlock();
            logger.info("Released Hazelcast lock for batch {}", batchId);
        }
    }
}
