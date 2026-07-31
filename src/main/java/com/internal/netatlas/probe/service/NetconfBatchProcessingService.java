package com.internal.netatlas.probe.service;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.repository.NormalizedRecordRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class NetconfBatchProcessingService {

    private final NormalizedRecordRepository repository;
    private final MeterRegistry meterRegistry;

    public NetconfBatchProcessingService(NormalizedRecordRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Persists the given NormalizedRecord into Cassandra and records a metric snapshot.
     *
     * @param record the DTO to persist
     * @return the persisted entity
     */
    public NormalizedRecord persistAndRecord(NormalizedRecord record) {
        NormalizedRecord saved = repository.save(record);
        // Record a counter metric for each persisted record, tagging by device family (canonical type)
        meterRegistry.counter("netconf.normalized.record.persisted",
                "deviceFamily", record.getCanonicalType() != null ? record.getCanonicalType() : "unknown")
                .increment();
        return saved;
    }
}
