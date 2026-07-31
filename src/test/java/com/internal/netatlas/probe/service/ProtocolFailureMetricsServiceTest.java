package com.internal.netatlas.probe.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link ProtocolFailureMetricsService} verifying that each protocol
 * failure increments the correct Micrometer counter.
 */
class ProtocolFailureMetricsServiceTest {

    private SimpleMeterRegistry registry;
    private ProtocolFailureMetricsService service;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        Counter netconf = registry.counter("probe.protocol.failures", "protocol", "netconf");
        Counter ssh = registry.counter("probe.protocol.failures", "protocol", "ssh");
        Counter snmp = registry.counter("probe.protocol.failures", "protocol", "snmp");
        Counter eapi = registry.counter("probe.protocol.failures", "protocol", "eapi");
        Counter grpc = registry.counter("probe.protocol.failures", "protocol", "grpc");
        service = new ProtocolFailureMetricsService(netconf, ssh, snmp, eapi, grpc);
    }

    @Test
    void testNetconfFailureIncrementsCounter() {
        service.recordFailure(ProtocolFailureMetricsService.Protocol.NETCONF);
        assertEquals(1.0, registry.get("probe.protocol.failures").tag("protocol", "netconf").counter().count());
    }

    @Test
    void testAllProtocolsIncrementCorrectly() {
        service.recordFailure(ProtocolFailureMetricsService.Protocol.SSH);
        service.recordFailure(ProtocolFailureMetricsService.Protocol.SNMP);
        service.recordFailure(ProtocolFailureMetricsService.Protocol.EAPI);
        service.recordFailure(ProtocolFailureMetricsService.Protocol.GRPC);
        assertEquals(1.0, registry.get("probe.protocol.failures").tag("protocol", "ssh").counter().count());
        assertEquals(1.0, registry.get("probe.protocol.failures").tag("protocol", "snmp").counter().count());
        assertEquals(1.0, registry.get("probe.protocol.failures").tag("protocol", "eapi").counter().count());
        assertEquals(1.0, registry.get("probe.protocol.failures").tag("protocol", "grpc").counter().count());
    }
}
