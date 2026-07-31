package com.internal.netatlas.probe.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer bean definitions exposing failure counters for each protocol used by the Device‑Probe service.
 * The counters are exposed automatically via Spring Boot Actuator at
 * {@code /actuator/metrics/probe.protocol.failures} with a {@code protocol} tag.
 */
@Configuration
public class MicrometerMetricsBeansActuatorEndpoint {

    @Bean
    public Counter netconfFailureCounter(MeterRegistry registry) {
        return Counter.builder("probe.protocol.failures")
                .description("Number of failed NETCONF protocol executions")
                .tag("protocol", "netconf")
                .register(registry);
    }

    @Bean
    public Counter sshFailureCounter(MeterRegistry registry) {
        return Counter.builder("probe.protocol.failures")
                .description("Number of failed SSH protocol executions")
                .tag("protocol", "ssh")
                .register(registry);
    }

    @Bean
    public Counter snmpFailureCounter(MeterRegistry registry) {
        return Counter.builder("probe.protocol.failures")
                .description("Number of failed SNMP protocol executions")
                .tag("protocol", "snmp")
                .register(registry);
    }

    @Bean
    public Counter eapiFailureCounter(MeterRegistry registry) {
        return Counter.builder("probe.protocol.failures")
                .description("Number of failed Arista eAPI protocol executions")
                .tag("protocol", "eapi")
                .register(registry);
    }

    @Bean
    public Counter grpcFailureCounter(MeterRegistry registry) {
        return Counter.builder("probe.protocol.failures")
                .description("Number of failed gRPC protocol executions")
                .tag("protocol", "grpc")
                .register(registry);
    }
}
