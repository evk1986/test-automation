package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.cp.lock.FencedLock;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProbeTestsNetconfService {

    private static final Logger LOG = LoggerFactory.getLogger(ProbeTestsNetconfService.class);

    private final NetconfAdapter netconfAdapter;
    private final HazelcastInstance hazelcastInstance;
    private final MeterRegistry meterRegistry;

    public ProbeTestsNetconfService(NetconfAdapter netconfAdapter,
                                    HazelcastInstance hazelcastInstance,
                                    MeterRegistry meterRegistry) {
        this.netconfAdapter = netconfAdapter;
        this.hazelcastInstance = hazelcastInstance;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Executes a NETCONF job while guaranteeing exclusive access per job ID.
     * The method records lock acquisition/release metrics and updates Micrometer counters.
     */
    public void processNetconfJob(ProbeJobMessage message) {
        String lockName = "netconf-lock-" + message.getJobId();
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(lockName);
        LOG.debug("Attempting to acquire lock {} for job {}", lockName, message.getJobId());
        lock.lock();
        try {
            meterRegistry.counter("probe.netconf.lock.acquired").increment();
            LOG.info("Lock acquired for NETCONF job {}", message.getJobId());
            // Execute the NETCONF session via the adapter
            netconfAdapter.execute(message.getDeviceId(), message.getCredentials());
            meterRegistry.counter("probe.netconf.session.success").increment();
            LOG.info("NETCONF session succeeded for device {} (job {})", message.getDeviceId(), message.getJobId());
        } finally {
            lock.unlock();
            meterRegistry.counter("probe.netconf.lock.released").increment();
            LOG.debug("Lock released for job {}", message.getJobId());
        }
    }
}
