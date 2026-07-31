package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LocalHazelcastVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(LocalHazelcastVerificationService.class);
    private static final String LOCK_MAP_NAME = "device-probe-locks";

    private final HazelcastInstance hazelcastInstance;
    private final MeterRegistry meterRegistry;

    public LocalHazelcastVerificationService(HazelcastInstance hazelcastInstance,
                                             MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Verifies that the local Hazelcast client can access the expected lock map
     * and registers a gauge for its size. Returns true when the map is reachable.
     */
    public boolean verifyLocalHazelcastConfig() {
        try {
            IMap<Object, Object> lockMap = hazelcastInstance.getMap(LOCK_MAP_NAME);
            if (lockMap == null) {
                logger.error("Hazelcast lock map '{}' not found", LOCK_MAP_NAME);
                return false;
            }
            int size = lockMap.size();
            logger.info("Hazelcast lock map '{}' size: {}", LOCK_MAP_NAME, size);
            Gauge.builder("hazelcast.map." + LOCK_MAP_NAME + ".size", lockMap, IMap::size)
                 .description("Size of the Hazelcast lock map used by Device-Probe")
                 .register(meterRegistry);
            return true;
        } catch (Exception e) {
            logger.error("Failed to verify Hazelcast configuration", e);
            return false;
        }
    }
}
