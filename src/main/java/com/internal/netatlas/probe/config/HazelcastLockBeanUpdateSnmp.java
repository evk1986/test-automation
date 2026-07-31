package com.internal.netatlas.probe.config;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastLockBeanUpdateSnmp {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastLockBeanUpdateSnmp(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Bean(name = "snmpWalkLock")
    public ILock snmpWalkLock() {
        // Global lock name for SNMP walk concurrency control
        return hazelcastInstance.getLock("snmp-walk-global-lock");
    }
}
