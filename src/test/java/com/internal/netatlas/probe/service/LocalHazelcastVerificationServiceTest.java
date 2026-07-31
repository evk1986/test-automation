package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LocalHazelcastVerificationServiceTest {

    private HazelcastInstance hazelcastInstance;
    private IMap<Object, Object> lockMap;
    private MeterRegistry meterRegistry;
    private LocalHazelcastVerificationService service;

    @BeforeEach
    void setUp() {
        hazelcastInstance = Mockito.mock(HazelcastInstance.class);
        lockMap = Mockito.mock(IMap.class);
        meterRegistry = new SimpleMeterRegistry();

        Mockito.when(hazelcastInstance.getMap("device-probe-locks")).thenReturn(lockMap);
        Mockito.when(lockMap.size()).thenReturn(3);

        service = new LocalHazelcastVerificationService(hazelcastInstance, meterRegistry);
    }

    @Test
    void verifyLocalHazelcastConfig_successful() {
        boolean result = service.verifyLocalHazelcastConfig();
        assertTrue(result, "Verification should succeed when map is present");
        assertNotNull(meterRegistry.get("hazelcast.map.device-probe-locks.size").gauge(),
                "Gauge for lock map size should be registered");
    }

    @Test
    void verifyLocalHazelcastConfig_missingMap() {
        Mockito.when(hazelcastInstance.getMap("device-probe-locks")).thenReturn(null);
        boolean result = service.verifyLocalHazelcastConfig();
        assertFalse(result, "Verification should fail when map is missing");
    }
}
