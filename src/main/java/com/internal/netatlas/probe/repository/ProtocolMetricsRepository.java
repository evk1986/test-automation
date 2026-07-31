package com.internal.netatlas.probe.repository;

import java.util.List;

public interface ProtocolMetricsRepository {

    List<ProtocolFailureCount> findAllFailureCounts();

    record ProtocolFailureCount(String protocol, Integer count) {}
}
