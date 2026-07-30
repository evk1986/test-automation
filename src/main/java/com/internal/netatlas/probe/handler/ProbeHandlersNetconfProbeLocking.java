package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.internal.netatlas.probe.service.NetconfProbeProcessingService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class ProbeHandlersNetconfProbeLocking {

    private static final Logger logger = LoggerFactory.getLogger(ProbeHandlersNetconfProbeLocking.class);
    private static final long LOCK_WAIT_SECONDS = 5L;

    private final HazelcastInstance hazelcastInstance;
    private final ProbeJobRepository probeJobRepository;
    private final NetconfProbeProcessingService processingService;
    private final Counter lockContentionCounter;
    private final Counter idempotentSkipCounter;

    @Autowired
    public ProbeHandlersNetconfProbeLocking(HazelcastInstance hazelcastInstance,
                                            ProbeJobRepository probeJobRepository,
                                            NetconfProbeProcessingService processingService,
                                            MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.probeJobRepository = probeJobRepository;
        this.processingService = processingService;
        this.lockContentionCounter = meterRegistry.counter("netconf.probe.lock.contention");
        this.idempotentSkipCounter = meterRegistry.counter("netconf.probe.idempotent.skip");
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        String lockKey = "netconf-probe-lock-" + message.getJobId();
        ILock lock = hazelcastInstance.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(LOCK_WAIT_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
            if (!acquired) {
                logger.warn("Could not acquire lock for job {} – contention recorded", message.getJobId());
                lockContentionCounter.increment();
                return;
            }

            // Idempotency check – if job already succeeded, skip processing
            return probeJobRepository.findById(message.getJobId())
                .filter(job -> job.getStatus() != null && job.getStatus().name().equals("SUCCESS"))
                .map(job -> {
                    logger.info("Job {} already completed successfully – idempotent skip", message.getJobId());
                    idempotentSkipCounter.increment();
                    return null; // skip further processing
                })
                .orElseGet(() -> {
                    // Proceed with actual NETCONF probe processing
                    processingService.process(message);
                    return null;
                });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while waiting for lock on job {}", message.getJobId(), e);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}
