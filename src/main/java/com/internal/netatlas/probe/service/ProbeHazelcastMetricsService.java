package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.repository.ProbeLockLogRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class ProbeHazelcastMetricsService {

    private final MeterRegistry meterRegistry;
    private final ProbeLockLogRepository lockLogRepository;

    public ProbeHazelcastMetricsService(MeterRegistry meterRegistry,
                                         ProbeLockLogRepository lockLogRepository) {
        this.meterRegistry = meterRegistry;
        this.lockLogRepository = lockLogRepository;
    }

    /**
     * Retrieves the current value of the {@code probe.protocol.failures} counter together with its tags.
     * The method also demonstrates a simple read from the lock‑log repository to prove the Hazelcast client
     * is functional in the local environment.
     */
    public ProtocolFailureMetricDto getProtocolFailureMetrics() {
        Counter counter = meterRegistry.find("probe.protocol.failures").counter();
        double value = counter != null ? counter.count() : 0.0;
        // Example tag extraction – in a real deployment the counter may have multiple tag dimensions.
        Map<String, String> tags = counter != null && counter.getId().getTags() != null
                ? counter.getId().getTags().stream().collect(
                        java.util.stream.Collectors.toMap(
                                t -> t.getKey(),
                                t -> t.getValue()))
                : Collections.emptyMap();
        // Simple repository call to prove Hazelcast connectivity; result is ignored for the metric response.
        lockLogRepository.count();
        return new ProtocolFailureMetricDto("probe.protocol.failures", value, tags);
    }

    public static class ProtocolFailureMetricDto {
        private final String metricName;
        private final double value;
        private final Map<String, String> tags;

        public ProtocolFailureMetricDto(String metricName, double value, Map<String, String> tags) {
            this.metricName = metricName;
            this.value = value;
            this.tags = tags;
        }

        public String getMetricName() {
            return metricName;
        }

        public double getValue() {
            return value;
        }

        public Map<String, String> getTags() {
            return tags;
        }
    }
}
