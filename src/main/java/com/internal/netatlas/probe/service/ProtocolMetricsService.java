package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.repository.ProtocolMetricsRepository;
import com.internal.netatlas.probe.repository.ProtocolMetricsRepository.ProtocolFailureCount;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProtocolMetricsService {

    private final ProtocolMetricsRepository repository;

    public ProtocolMetricsService(ProtocolMetricsRepository repository) {
        this.repository = repository;
    }

    public Map<String, Integer> fetchFailureMetrics() {
        List<ProtocolFailureCount> counts = repository.findAllFailureCounts();
        Map<String, Integer> result = new HashMap<>();
        for (ProtocolFailureCount c : counts) {
            result.put(c.protocol(), c.count());
        }
        return result;
    }
}
