package com.internal.netatlas.probe.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

/**
 * Service responsible for recording protocol failure metrics.
 * The metric name is {@code probe.protocol.failures} and is tagged by the protocol name.
 */
@Service
public class ProtocolFailureMetricsService {

    private final MeterRegistry meterRegistry;

    public ProtocolFailureMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Increment the failure counter for the given protocol.
     *
     * @param protocol the protocol that failed (e.g., SNMP, NETCONF)
     */
    public void recordFailure(String protocol) {
        Counter counter = Counter.builder("probe.protocol.failures")
                .description("Number of protocol failures observed by Device-Probe")
                .tag("protocol", protocol)
                .register(meterRegistry);
        counter.increment();
    }
}
